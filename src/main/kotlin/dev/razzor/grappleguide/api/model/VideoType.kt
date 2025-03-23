package dev.razzor.grappleguide.api.model

/**
 * Enum representing different types of video sources
 */
enum class VideoType {
    YOUTUBE,
    VIMEO,
    OTHER;
    
    companion object {
        private val YOUTUBE_PATTERNS = listOf(
            "youtube\\.com",
            "youtu\\.be"
        )
        
        private val VIMEO_PATTERNS = listOf(
            "vimeo\\.com"
        )
        
        /**
         * Determine the video type from a URL
         */
        fun fromUrl(url: String): VideoType {
            return when {
                YOUTUBE_PATTERNS.any { url.contains(Regex(it)) } -> YOUTUBE
                VIMEO_PATTERNS.any { url.contains(Regex(it)) } -> VIMEO
                else -> OTHER
            }
        }
    }
} 