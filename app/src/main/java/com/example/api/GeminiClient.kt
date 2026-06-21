package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Checks if the API key is configured and is not the placeholder value
     */
    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.contains("PLACEHOLDER")
    }

    /**
     * Call Google Gemini API to get intelligent text completions.
     * Uses org.json for robust and dependency-free parsing.
     */
    suspend fun generateContent(
        systemInstruction: String,
        userPrompt: String,
        temperature: Double = 0.7
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!isApiKeyConfigured()) {
            Log.e(TAG, "Gemini API key is not configured. Utilizing fallback model.")
            throw IllegalStateException("APIKEY_NOT_SET")
        }

        val url = "$BASE_URL?key=$apiKey"

        // Build the JSON request body
        val requestJson = JSONObject().apply {
            // Contents
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            // System Instruction
            if (systemInstruction.isNotEmpty()) {
                val systemInstructionObj = JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                }
                put("systemInstruction", systemInstructionObj)
            }

            // Generation Config
            val configObj = JSONObject().apply {
                put("temperature", temperature)
            }
            put("generationConfig", configObj)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API failure: Code ${response.code}, Body: $errBody")
                    throw IOException("HTTP_${response.code}: $errBody")
                }

                val responseBodyStr = response.body?.string()
                    ?: throw IOException("Empty response body")

                Log.d(TAG, "Response size: ${responseBodyStr.length} chars")

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "Error: No generation candidates."
                }

                val firstCandidate = candidates.getJSONObject(0)
                val responseContent = firstCandidate.optJSONObject("content")
                if (responseContent == null) {
                    return@withContext "Error: Candidate has no content."
                }

                val parts = responseContent.optJSONArray("parts")
                if (parts == null || parts.length() == 0) {
                    return@withContext "Error: Content has no parts."
                }

                val text = parts.getJSONObject(0).optString("text")
                return@withContext text.trim()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing API request", e)
            throw e
        }
    }
}
