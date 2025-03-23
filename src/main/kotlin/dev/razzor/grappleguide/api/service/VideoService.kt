package dev.razzor.grappleguide.api.service

import dev.razzor.grappleguide.api.dto.VideoCreateDTO
import dev.razzor.grappleguide.api.dto.VideoDTO
import dev.razzor.grappleguide.api.dto.VideoUpdateDTO
import dev.razzor.grappleguide.api.exception.ResourceNotFoundException
import dev.razzor.grappleguide.api.model.Video
import dev.razzor.grappleguide.api.repository.CategoryRepository
import dev.razzor.grappleguide.api.repository.VideoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val categoryRepository: CategoryRepository,
    private val youTubeService: YouTubeService
) {

    fun getAllVideos(): List<VideoDTO> {
        return videoRepository.findAll().map { VideoDTO.fromEntity(it) }
    }

    fun getVideoById(id: Long): VideoDTO {
        val video = videoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Video not found with id: $id") }
        return VideoDTO.fromEntity(video)
    }

    fun getVideosByCategory(categoryId: Long): List<VideoDTO> {
        if (!categoryRepository.existsById(categoryId)) {
            throw ResourceNotFoundException("Category not found with id: $categoryId")
        }
        return videoRepository.findByCategoryId(categoryId).map { VideoDTO.fromEntity(it) }
    }

    fun searchVideosByTitle(title: String): List<VideoDTO> {
        return videoRepository.findByTitleContainingIgnoreCase(title).map { VideoDTO.fromEntity(it) }
    }

    @Transactional
    fun createVideo(videoCreateDTO: VideoCreateDTO): VideoDTO {
        val category = videoCreateDTO.categoryId?.let {
            categoryRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Category not found with id: $it") }
        }

        // Extract video data from YouTube if it's a YouTube URL
        val youtubeVideoId = youTubeService.extractVideoId(videoCreateDTO.url)
        val videoDetails = youtubeVideoId?.let { youTubeService.getVideoDetails(it) }
        
        val video = Video(
            // Use provided title or YouTube title if available
            title = videoCreateDTO.title.takeIf { it.isNotBlank() } ?: videoDetails?.title ?: videoCreateDTO.title,
            // Use provided description or YouTube description if available
            description = videoCreateDTO.description ?: videoDetails?.description,
            url = videoCreateDTO.url,
            // Use provided duration or YouTube duration if available
            durationSeconds = videoCreateDTO.durationSeconds ?: videoDetails?.durationSeconds,
            category = category
        )

        val savedVideo = videoRepository.save(video)
        return VideoDTO.fromEntity(savedVideo)
    }

    @Transactional
    fun updateVideo(id: Long, videoUpdateDTO: VideoUpdateDTO): VideoDTO {
        val existingVideo = videoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Video not found with id: $id") }

        // Handle category update
        val category = videoUpdateDTO.categoryId?.let {
            categoryRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Category not found with id: $it") }
        }

        // If URL is being updated and it's a YouTube URL, try to fetch updated metadata
        var youtubeVideoDetails: YouTubeVideoDetails? = null
        if (videoUpdateDTO.url != null && videoUpdateDTO.url != existingVideo.url) {
            val youtubeVideoId = youTubeService.extractVideoId(videoUpdateDTO.url)
            youtubeVideoDetails = youtubeVideoId?.let { youTubeService.getVideoDetails(it) }
        }

        // Update the fields if they are not null
        if (videoUpdateDTO.title != null) {
            existingVideo.title = videoUpdateDTO.title
        } else if (youtubeVideoDetails != null && existingVideo.title.isBlank()) {
            // Update with YouTube title if current title is blank
            existingVideo.title = youtubeVideoDetails.title
        }
        
        if (videoUpdateDTO.description != null) {
            existingVideo.description = videoUpdateDTO.description
        } else if (youtubeVideoDetails != null && existingVideo.description.isNullOrBlank()) {
            // Update with YouTube description if current description is blank
            existingVideo.description = youtubeVideoDetails.description
        }
        
        videoUpdateDTO.url?.let { existingVideo.url = it }
        
        if (videoUpdateDTO.durationSeconds != null) {
            existingVideo.durationSeconds = videoUpdateDTO.durationSeconds
        } else if (youtubeVideoDetails != null && existingVideo.durationSeconds == null) {
            // Update with YouTube duration if current duration is null
            existingVideo.durationSeconds = youtubeVideoDetails.durationSeconds
        }
        
        if (videoUpdateDTO.categoryId != null) {
            existingVideo.category = category
        }
        
        existingVideo.updatedAt = LocalDateTime.now()

        val updatedVideo = videoRepository.save(existingVideo)
        return VideoDTO.fromEntity(updatedVideo)
    }

    @Transactional
    fun deleteVideo(id: Long) {
        if (!videoRepository.existsById(id)) {
            throw ResourceNotFoundException("Video not found with id: $id")
        }
        videoRepository.deleteById(id)
    }
} 