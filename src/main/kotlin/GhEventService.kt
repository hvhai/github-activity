package com.codehunter.github_activity_kotlin

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class GhEventService {
    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun getEvents(username: String): List<GhEvent> {
        val request = Request.Builder()
            .url("https://api.github.com/users/$username/events")
            .build();
        client.newCall(request).execute().use { response ->
            require(response.isSuccessful) {
                "Failed to get events: statusCode=${response.code}: Message=${response.message}"
            }
            val bodyString = response.body?.string()
            return bodyString?.let { body -> json.decodeFromString<List<GhEvent>>(body) }
                ?: emptyList()
        }
    }
}