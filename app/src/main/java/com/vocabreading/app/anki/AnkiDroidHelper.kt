package com.vocabreading.app.anki

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.vocabreading.app.model.DetectedWord

class AnkiDroidHelper(private val context: Context) {

    companion object {
        private const val ANKI_PACKAGE = "com.ichi2.anki"
        private val NOTES_URI = Uri.parse("content://com.ichi2.anki.flashcards/notes")
        private val DECKS_URI = Uri.parse("content://com.ichi2.anki.flashcards/decks")
        private val MODELS_URI = Uri.parse("content://com.ichi2.anki.flashcards/models")
    }

    fun isAnkiDroidInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(ANKI_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getDeckId(deckName: String): Long? {
        val cursor = context.contentResolver.query(
            DECKS_URI,
            arrayOf("deck_id", "deck_name"),
            null, null, null
        ) ?: return null

        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("deck_name"))
                if (name == deckName) {
                    return it.getLong(it.getColumnIndexOrThrow("deck_id"))
                }
            }
        }
        return null
    }

    fun getModelId(modelName: String): Long? {
        val cursor = context.contentResolver.query(
            MODELS_URI,
            arrayOf("model_id", "model_name"),
            null, null, null
        ) ?: return null

        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("model_name"))
                if (name == modelName) {
                    return it.getLong(it.getColumnIndexOrThrow("model_id"))
                }
            }
        }
        return null
    }

    /**
     * Add a flashcard to AnkiDroid.
     * Front: original word/phrase in source language
     * Back: English translation
     */
    fun addFlashcard(
        word: DetectedWord,
        deckId: Long,
        modelId: Long
    ): Boolean {
        val translatedText = word.translatedText ?: return false

        val values = ContentValues().apply {
            put("mid", modelId)
            put("did", deckId)
            put("flds", "${word.originalText}\u001f$translatedText")
            put("tags", "vocab_reading")
        }

        return try {
            val uri = context.contentResolver.insert(NOTES_URI, values)
            uri != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Add multiple flashcards at once.
     * Returns the number of successfully added cards.
     */
    fun addFlashcards(
        words: List<DetectedWord>,
        deckName: String,
        modelName: String
    ): Result<Int> {
        if (!isAnkiDroidInstalled()) {
            return Result.failure(Exception("AnkiDroid is not installed"))
        }

        val deckId = getDeckId(deckName)
            ?: return Result.failure(Exception("Deck '$deckName' not found in AnkiDroid. Please create it first."))

        val modelId = getModelId(modelName)
            ?: return Result.failure(Exception("Note type '$modelName' not found in AnkiDroid."))

        var successCount = 0
        for (word in words) {
            if (word.translatedText != null && addFlashcard(word, deckId, modelId)) {
                successCount++
            }
        }

        return Result.success(successCount)
    }
}
