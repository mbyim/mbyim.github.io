package com.vocabreading.app.detection

import android.graphics.Bitmap
import android.graphics.Rect
import com.vocabreading.app.model.CircledRegion
import com.vocabreading.app.model.DetectedWord
import com.vocabreading.app.model.TextBlock
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class CircleDetector {

    /**
     * Detects hand-drawn circles or ellipses in the image.
     *
     * Strategy:
     * 1. Convert to grayscale and apply adaptive thresholding to isolate drawn marks
     * 2. Find contours in the thresholded image
     * 3. Filter contours by area, aspect ratio, and solidity to identify circle-like shapes
     * 4. Return bounding boxes of detected circular contours
     */
    fun detectCircledRegions(bitmap: Bitmap): List<CircledRegion> {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)

        // Apply Gaussian blur to reduce noise
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

        // Adaptive threshold to isolate drawn marks (pen/pencil circles)
        val thresh = Mat()
        Imgproc.adaptiveThreshold(
            blurred, thresh, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            15, 10.0
        )

        // Morphological operations to connect broken circle segments
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(3.0, 3.0))
        val morphed = Mat()
        Imgproc.morphologyEx(thresh, morphed, Imgproc.MORPH_CLOSE, kernel)

        // Find contours
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            morphed, contours, hierarchy,
            Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        )

        val imageArea = bitmap.width.toDouble() * bitmap.height.toDouble()
        val regions = mutableListOf<CircledRegion>()

        for (contour in contours) {
            val area = Imgproc.contourArea(contour)

            // Filter by area: circles around words should be a reasonable size
            // Not too small (noise) or too large (page border)
            if (area < imageArea * 0.001 || area > imageArea * 0.3) continue

            val rect = Imgproc.boundingRect(contour)
            val aspectRatio = rect.width.toDouble() / rect.height.toDouble()

            // Circle-like shapes have aspect ratios between 0.3 and 3.0
            if (aspectRatio < 0.3 || aspectRatio > 3.0) continue

            // Check solidity (area / convex hull area) - circles tend to have lower
            // solidity than filled shapes because they're hollow
            val hull = MatOfPoint()
            Imgproc.convexHull(contour, hull)
            val hullArea = Imgproc.contourArea(hull)
            val solidity = if (hullArea > 0) area / hullArea else 0.0

            // Hand-drawn circles: moderate solidity (they're not perfectly filled)
            if (solidity < 0.15 || solidity > 0.85) continue

            // Check perimeter-to-area ratio to favor ring-like shapes
            val perimeter = Imgproc.arcLength(
                org.opencv.core.MatOfPoint2f(*contour.toArray()), true
            )
            val circularity = 4 * Math.PI * area / (perimeter * perimeter)

            // Relaxed circularity for hand-drawn circles
            if (circularity < 0.1) continue

            regions.add(
                CircledRegion(
                    centerX = rect.x + rect.width / 2,
                    centerY = rect.y + rect.height / 2,
                    radiusX = rect.width / 2,
                    radiusY = rect.height / 2,
                    boundingBox = Rect(
                        rect.x, rect.y,
                        rect.x + rect.width,
                        rect.y + rect.height
                    )
                )
            )
        }

        // Clean up
        mat.release()
        gray.release()
        blurred.release()
        thresh.release()
        morphed.release()
        hierarchy.release()

        return regions
    }

    /**
     * Match OCR text blocks to circled regions.
     * A text block is considered "circled" if its bounding box significantly
     * overlaps with a detected circle region.
     */
    fun matchTextToCircles(
        textBlocks: List<TextBlock>,
        circledRegions: List<CircledRegion>
    ): List<DetectedWord> {
        if (circledRegions.isEmpty()) return emptyList()

        val matchedWords = mutableListOf<DetectedWord>()

        for (textBlock in textBlocks) {
            for (region in circledRegions) {
                if (isTextInsideCircle(textBlock.boundingBox, region.boundingBox)) {
                    matchedWords.add(
                        DetectedWord(
                            originalText = textBlock.text,
                            boundingBox = textBlock.boundingBox
                        )
                    )
                    break
                }
            }
        }

        // Merge adjacent matched words that were inside the same circle
        return mergeAdjacentWords(matchedWords, circledRegions)
    }

    private fun isTextInsideCircle(textBox: Rect, circleBox: Rect): Boolean {
        // Calculate intersection
        val intersectLeft = maxOf(textBox.left, circleBox.left)
        val intersectTop = maxOf(textBox.top, circleBox.top)
        val intersectRight = minOf(textBox.right, circleBox.right)
        val intersectBottom = minOf(textBox.bottom, circleBox.bottom)

        if (intersectLeft >= intersectRight || intersectTop >= intersectBottom) return false

        val intersectionArea = (intersectRight - intersectLeft).toLong() *
                (intersectBottom - intersectTop).toLong()
        val textArea = textBox.width().toLong() * textBox.height().toLong()

        if (textArea == 0L) return false

        // Text is "inside" the circle if >= 50% of the text box overlaps
        return intersectionArea.toDouble() / textArea.toDouble() >= 0.5
    }

    /**
     * Merge adjacent words that fall within the same circled region
     * into phrases (e.g., "por" + "favor" → "por favor").
     */
    private fun mergeAdjacentWords(
        words: List<DetectedWord>,
        circledRegions: List<CircledRegion>
    ): List<DetectedWord> {
        if (words.size <= 1) return words

        val result = mutableListOf<DetectedWord>()
        val used = BooleanArray(words.size)

        for (region in circledRegions) {
            val wordsInRegion = words.indices.filter { i ->
                !used[i] && words[i].boundingBox != null &&
                        isTextInsideCircle(words[i].boundingBox!!, region.boundingBox)
            }

            if (wordsInRegion.isEmpty()) continue

            // Sort by horizontal position (left-to-right reading order)
            val sorted = wordsInRegion.sortedBy { words[it].boundingBox!!.left }

            val mergedText = sorted.joinToString(" ") { words[it].originalText }

            // Union of all bounding boxes
            val mergedBox = Rect(
                sorted.minOf { words[it].boundingBox!!.left },
                sorted.minOf { words[it].boundingBox!!.top },
                sorted.maxOf { words[it].boundingBox!!.right },
                sorted.maxOf { words[it].boundingBox!!.bottom }
            )

            result.add(DetectedWord(originalText = mergedText, boundingBox = mergedBox))
            sorted.forEach { used[it] = true }
        }

        // Add any unmatched words individually
        words.indices.filter { !used[it] }.forEach { result.add(words[it]) }

        return result
    }
}
