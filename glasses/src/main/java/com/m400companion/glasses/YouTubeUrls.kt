package com.m400companion.glasses

import android.net.Uri

object YouTubeUrls {

    /**
     * Normalizes any YouTube link (youtu.be, shorts, live, embed, watch) to a
     * mobile watch URL, preserving the timestamp if present.
     */
    fun toWatchUrl(raw: String): String {
        val uri = Uri.parse(raw.trim())
        val host = uri.host?.lowercase() ?: return raw
        if (host != "youtu.be" && !host.endsWith("youtube.com")) return raw

        val segments = uri.pathSegments
        val videoId = when {
            host == "youtu.be" -> segments.firstOrNull()
            segments.firstOrNull() == "watch" -> uri.getQueryParameter("v")
            segments.firstOrNull() in listOf("shorts", "live", "embed") -> segments.getOrNull(1)
            else -> uri.getQueryParameter("v")
        }
        if (videoId.isNullOrBlank()) return raw

        val timestamp = uri.getQueryParameter("t")?.let { "&t=$it" } ?: ""
        return "https://m.youtube.com/watch?v=$videoId$timestamp"
    }
}
