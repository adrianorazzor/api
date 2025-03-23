package dev.razzor.grappleguide.api.dto

import dev.razzor.grappleguide.api.model.Video
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class VideoDTO(
    val id: Long?,
    val title: String,
    val description: String?,
    val url: String,
    val durationSeconds: Int?,
    val categoryId: Long?,
    val categoryName: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(video: Video): VideoDTO {
            return VideoDTO(
                id = video.id,
                title = video.title,
                description = video.description,
                url = video.url,
                durationSeconds = video.durationSeconds,
                categoryId = video.category?.id,
                categoryName = video.category?.name,
                createdAt = video.createdAt,
                updatedAt = video.updatedAt
            )
        }
    }
}

data class VideoCreateDTO(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 200, message = "Title must be less than 200 characters")
    val title: String,
    
    @field:Size(max = 1000, message = "Description must be less than 1000 characters")
    val description: String? = null,
    
    @field:NotBlank(message = "URL is required")
    val url: String,
    
    val durationSeconds: Int? = null,
    
    val categoryId: Long? = null
)

data class VideoUpdateDTO(
    @field:Size(max = 200, message = "Title must be less than 200 characters")
    val title: String? = null,
    
    @field:Size(max = 1000, message = "Description must be less than 1000 characters")
    val description: String? = null,
    
    val url: String? = null,
    
    val durationSeconds: Int? = null,
    
    val categoryId: Long? = null
) 