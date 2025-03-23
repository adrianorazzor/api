package dev.razzor.grappleguide.api.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoListResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class YouTubeService(
    @Value("\${youtube.api.key}") private val apiKey: String
) {
    private val youtube: YouTube by lazy {
        YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null
        )
            .setApplicationName("Grapple Guide")
            .build()
    }

    /**
     * Extract video ID from YouTube URL
     */
    fun extractVideoId(url: String): String? {
        val regex = Regex("""(?:youtube\.com\/(?:[^\/\n\s]+\/\S+\/|(?:v|e(?:mbed)?)\/|\S*?[?&]v=)|youtu\.be\/)([a-zA-Z0-9_-]{11})""")
        return regex.find(url)?.groupValues?.get(1)
    }

    /**
     * Get video details from YouTube API
     */
    fun getVideoDetails(videoId: String): YouTubeVideoDetails? {
        try {
            val response: VideoListResponse = youtube.videos()
                .list(listOf("snippet", "contentDetails"))
                .setKey(apiKey)
                .setId(listOf(videoId))
                .execute()

            if (response.items.isNullOrEmpty()) {
                return null
            }

            val video = response.items.first()
            val durationStr = video.contentDetails.duration // ISO 8601 duration format
            val title = video.snippet.title
            val description = video.snippet.description

            // Parse ISO 8601 duration
            val duration = Duration.parse(durationStr)
            val durationInSeconds = duration.seconds.toInt()

            return YouTubeVideoDetails(
                title = title,
                description = description,
                durationSeconds = durationInSeconds
            )
        } catch (e: Exception) {
            // Log error in production environment
            println("Error fetching YouTube video details: ${e.message}")
            return null
        }
    }
}

data class YouTubeVideoDetails(
    val title: String,
    val description: String,
    val durationSeconds: Int
) 