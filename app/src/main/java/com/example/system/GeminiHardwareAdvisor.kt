package com.example.system

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
import java.util.concurrent.TimeUnit

class GeminiHardwareAdvisor {

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    suspend fun getHardwareOptimizationTips(stats: LiveDeviceStats): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackAdvice(stats)
        }

        val prompt = """
            You are an expert Android Game Performance Engineer.
            Analyze these device specs:
            - Hardware/GPU: ${stats.gpuRenderer}
            - CPU: ${stats.cpuCores} cores
            - Total RAM: ${String.format("%.1f", stats.totalRamBytes / (1024.0 * 1024.0 * 1024.0))} GB (${stats.freeRamPercent}% free)
            - Screen: ${stats.screenResolution} @ ${stats.screenRefreshRateHz} Hz
            
            Provide 3 bullet points with specific graphic setting tuning recommendations:
            1. Resolution & Shader Scale
            2. FPS cap & Thermal Strategy
            3. Memory & Background Pipeline
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val httpRequest = Request.Builder()
                .url(url)
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val responseJson = JSONObject(responseBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return@withContext text.trim()
                        }
                    }
                }
            }
            Log.e("GeminiAdvisor", "Response unsuccessful or empty: code=${response.code}")
            generateFallbackAdvice(stats)
        } catch (e: Exception) {
            Log.e("GeminiAdvisor", "Gemini API request failed", e)
            generateFallbackAdvice(stats)
        }
    }

    private fun generateFallbackAdvice(stats: LiveDeviceStats): String {
        val ramGb = String.format("%.1f", stats.totalRamBytes / (1024.0 * 1024.0 * 1024.0))
        return """
            • Resolution & Shader Scale: For ${stats.gpuRenderer}, cap render scale to 85-90% native (${stats.screenResolution}) to maintain a solid ${stats.screenRefreshRateHz} FPS floor during intense action sequences.
            • FPS Cap & Thermal Strategy: Lock target framerate to ${stats.screenRefreshRateHz} Hz. Utilizing the Auto Eco Refresh Rate Limiter when battery drops below 20% mitigates thermal throttling.
            • Memory Pipeline: Device has $ramGb GB total RAM. Perform background memory optimization prior to gaming to keep at least 35% free headroom for shader cache.
        """.trimIndent()
    }
}
