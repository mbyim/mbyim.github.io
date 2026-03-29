package com.vocabreading.app.translation

import com.vocabreading.app.model.DetectedWord
import com.vocabreading.app.model.SupportedLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LibreTranslateService(
    private val baseUrl: String = "https://libretranslate.com"
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun translate(
        text: String,
        sourceLanguage: SupportedLanguage,
        targetLanguage: String = "en"
    ): String = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("q", text)
            put("source", sourceLanguage.code)
            put("target", targetLanguage)
            put("format", "text")
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/translate")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()
            ?: throw Exception("Empty response from LibreTranslate")

        if (!response.isSuccessful) {
            throw Exception("Translation failed (${response.code}): $body")
        }

        val jsonResponse = JSONObject(body)
        jsonResponse.getString("translatedText")
    }

    suspend fun translateWords(
        words: List<DetectedWord>,
        sourceLanguage: SupportedLanguage
    ): List<DetectedWord> {
        return words.map { word ->
            try {
                val translated = translate(word.originalText, sourceLanguage)
                word.copy(translatedText = translated)
            } catch (e: Exception) {
                word.copy(translatedText = "[Translation failed: ${e.message}]")
            }
        }
    }
}
