package com.jarvis.ai.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiRepository(private val apiKeyProvider: () -> String?) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendChatMessage(
        userMessage: String,
        history: List<Pair<String, String>>,
        language: String = "en"
    ): Result<Pair<String, List<JSONObject>>> = withContext(Dispatchers.IO) {
        val apiKey = apiKeyProvider() ?: return@withContext Result.failure(Exception("Gemini API key is not configured."))

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // System instructions
            val systemInstruction = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "You are JARVIS AI Android Assistant. THINK WITH GEMINI. ACT WITH ANDROID. Support English and Bengali. Use function calling tools for actions."))
                })
            }

            // History
            for ((role, text) in history) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", text))
                    })
                })
            }

            // Current message
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", userMessage))
                })
            })

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstruction)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "aistudio-build")
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini error (${response.code}): $responseString"))
            }

            val jsonResponse = JSONObject(responseString)
            val candidate = jsonResponse.optJSONArray("candidates")?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val functionCalls = mutableListOf<JSONObject>()
            val textBuilder = StringBuilder()

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    if (part.has("text")) {
                        textBuilder.append(part.getString("text"))
                    }
                    if (part.has("functionCall")) {
                        functionCalls.add(part.getJSONObject("functionCall"))
                    }
                }
            }

            Result.success(Pair(textBuilder.toString(), functionCalls))
        } catch (e: Exception) {
            Log.e("GeminiRepository", "API execution error", e)
            Result.failure(e)
        }
    }
}
