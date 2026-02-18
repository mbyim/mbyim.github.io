package com.vocabreading.app.model

data class AppSettings(
    val sourceLanguage: SupportedLanguage = SupportedLanguage.SPANISH,
    val libreTranslateUrl: String = "https://libretranslate.com",
    val ankiDeckName: String = "Vocab Reading",
    val ankiModelName: String = "Basic"
)
