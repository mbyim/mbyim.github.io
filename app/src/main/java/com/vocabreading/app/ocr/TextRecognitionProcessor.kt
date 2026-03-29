package com.vocabreading.app.ocr

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vocabreading.app.model.TextBlock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TextRecognitionProcessor {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(bitmap: Bitmap): List<TextBlock> = suspendCoroutine { cont ->
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val blocks = mutableListOf<TextBlock>()

                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        for (element in line.elements) {
                            val box = element.boundingBox
                            if (box != null) {
                                blocks.add(
                                    TextBlock(
                                        text = element.text,
                                        boundingBox = box
                                    )
                                )
                            }
                        }
                    }
                }

                cont.resume(blocks)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    fun close() {
        recognizer.close()
    }
}
