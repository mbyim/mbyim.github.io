package com.vocabreading.app.model

import android.graphics.Rect

data class DetectedWord(
    val originalText: String,
    val translatedText: String? = null,
    val boundingBox: Rect? = null,
    val isSelected: Boolean = true
)

data class ProcessingResult(
    val allText: List<TextBlock>,
    val circledRegions: List<CircledRegion>,
    val detectedWords: List<DetectedWord>
)

data class TextBlock(
    val text: String,
    val boundingBox: Rect
)

data class CircledRegion(
    val centerX: Int,
    val centerY: Int,
    val radiusX: Int,
    val radiusY: Int,
    val boundingBox: Rect
)

enum class SupportedLanguage(val code: String, val displayName: String, val mlKitCode: String) {
    SPANISH("es", "Spanish", "es"),
    FRENCH("fr", "French", "fr"),
    GERMAN("de", "German", "de"),
    ITALIAN("it", "Italian", "it"),
    PORTUGUESE("pt", "Portuguese", "pt"),
    JAPANESE("ja", "Japanese", "ja"),
    CHINESE("zh", "Chinese", "zh"),
    KOREAN("ko", "Korean", "ko"),
    RUSSIAN("ru", "Russian", "ru"),
    ARABIC("ar", "Arabic", "ar");

    companion object {
        fun fromCode(code: String): SupportedLanguage? =
            entries.find { it.code == code }
    }
}
