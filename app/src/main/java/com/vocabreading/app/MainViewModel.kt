package com.vocabreading.app

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabreading.app.anki.AnkiDroidHelper
import com.vocabreading.app.detection.CircleDetector
import com.vocabreading.app.model.AppSettings
import com.vocabreading.app.model.DetectedWord
import com.vocabreading.app.ocr.TextRecognitionProcessor
import com.vocabreading.app.translation.LibreTranslateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppScreen {
    data object Camera : AppScreen()
    data class Processing(val status: String) : AppScreen()
    data class Results(val words: List<DetectedWord>) : AppScreen()
    data object Settings : AppScreen()
}

class MainViewModel : ViewModel() {

    private val _screen = MutableStateFlow<AppScreen>(AppScreen.Camera)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    private val textProcessor = TextRecognitionProcessor()
    private val circleDetector = CircleDetector()

    private var currentWords = mutableListOf<DetectedWord>()

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    fun navigateToSettings() {
        _screen.value = AppScreen.Settings
    }

    fun navigateToCamera() {
        _screen.value = AppScreen.Camera
        currentWords.clear()
    }

    fun dismissSnackbar() {
        _snackbar.value = null
    }

    fun onPhotoCaptured(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Step 1: Detect circles
                _screen.value = AppScreen.Processing("Detecting circled words...")
                val circledRegions = circleDetector.detectCircledRegions(bitmap)

                if (circledRegions.isEmpty()) {
                    _screen.value = AppScreen.Results(emptyList())
                    return@launch
                }

                // Step 2: OCR
                _screen.value = AppScreen.Processing("Reading text...")
                val textBlocks = textProcessor.recognizeText(bitmap)

                // Step 3: Match text to circles
                _screen.value = AppScreen.Processing("Matching words to circles...")
                val matchedWords = circleDetector.matchTextToCircles(textBlocks, circledRegions)

                if (matchedWords.isEmpty()) {
                    _screen.value = AppScreen.Results(emptyList())
                    return@launch
                }

                // Step 4: Translate
                _screen.value = AppScreen.Processing(
                    "Translating ${matchedWords.size} word(s)..."
                )
                val settings = _settings.value
                val translator = LibreTranslateService(settings.libreTranslateUrl)
                val translatedWords = translator.translateWords(
                    matchedWords,
                    settings.sourceLanguage
                )

                currentWords = translatedWords.toMutableList()
                _screen.value = AppScreen.Results(translatedWords)

            } catch (e: Exception) {
                _snackbar.value = "Error: ${e.message}"
                _screen.value = AppScreen.Camera
            }
        }
    }

    fun toggleWord(index: Int) {
        if (index in currentWords.indices) {
            currentWords[index] = currentWords[index].let {
                it.copy(isSelected = !it.isSelected)
            }
            _screen.value = AppScreen.Results(currentWords.toList())
        }
    }

    fun createFlashcards(context: Context) {
        val selectedWords = currentWords.filter {
            it.isSelected && it.translatedText != null
        }

        if (selectedWords.isEmpty()) {
            _snackbar.value = "No words selected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val ankiHelper = AnkiDroidHelper(context)
            val settings = _settings.value

            val result = ankiHelper.addFlashcards(
                selectedWords,
                settings.ankiDeckName,
                settings.ankiModelName
            )

            result.fold(
                onSuccess = { count ->
                    _snackbar.value = "Created $count flashcard(s) in AnkiDroid"
                    _screen.value = AppScreen.Camera
                    currentWords.clear()
                },
                onFailure = { error ->
                    _snackbar.value = error.message
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        textProcessor.close()
    }
}
