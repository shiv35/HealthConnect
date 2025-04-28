package com.example.health_connect.ChatApplication

import android.util.Log
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object GeminiHelper {
    private const val GEMINI_API_KEY = "AIzaSyAJIssd1t7TQ3e1U6sUqQSVvNwZPXoIduo "
    private const val GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=$GEMINI_API_KEY"

    /**
     * Calls Gemini to classify spam.
     * @param message the chat text to check
     * @param callback returns true if spam, false otherwise
     */
    fun isSpam(message: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()

        // A clear instruction to return exactly "true" or "false"
        val prompt = buildString {
            append("You are a spam detector. ")
            append("Classify the following message as SPAM or NOT SPAM. ")
            append("Respond with exactly true if it is spam, or false if it is not spam. ")
            append("Message: \"$message\"")
        }

        // Gemini expects a JSON body under "contents" → list of parts
        val jsonBody = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": ${Gson().toJson(prompt)} }
                  ]
                }
              ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(GEMINI_URL)
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Log the error
                // You can use a logging library or Android's Log class
//                 Log.e("GeminiHelper", "API call failed: ${e.message}")
                // If the API fails, treat as non-spam by default
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("GeminiHelper3", "API call failed with code: ${response.code}, body: ${response.body?.string()}")
                    callback(false)
                    return
                }
                val body = response.body?.string()
                try {
                    val root = Gson().fromJson(body, JsonObject::class.java)
                    val text = root
                        .getAsJsonArray("candidates")
                        ?.get(0)?.asJsonObject
                        ?.getAsJsonObject("content")
                        ?.getAsJsonArray("parts")
                        ?.get(0)?.asJsonObject
                        ?.get("text")?.asString
                        ?.trim()
                        ?.lowercase()
                    // Only “true” means spam
                    callback(text == "true")
                } catch (e: Exception) {
//                    Log.e("GeminiHelper2", "API call failed: ${e.message}")

                    e.printStackTrace()
                    callback(false)
                }
            }
        })
    }
}
