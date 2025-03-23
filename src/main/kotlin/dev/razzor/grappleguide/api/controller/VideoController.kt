package dev.razzor.grappleguide.api.controller

import dev.razzor.grappleguide.api.dto.VideoCreateDTO
import dev.razzor.grappleguide.api.dto.VideoDTO
import dev.razzor.grappleguide.api.dto.VideoUpdateDTO
import dev.razzor.grappleguide.api.service.VideoService
import dev.razzor.grappleguide.api.service.YouTubeService
import dev.razzor.grappleguide.api.service.YouTubeVideoDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/videos")
class VideoController(
    private val videoService: VideoService,
    private val youTubeService: YouTubeService
) {

    @GetMapping
    fun getAllVideos(): ResponseEntity<List<VideoDTO>> {
        return ResponseEntity.ok(videoService.getAllVideos())
    }

    @GetMapping("/{id}")
    fun getVideoById(@PathVariable id: Long): ResponseEntity<VideoDTO> {
        return ResponseEntity.ok(videoService.getVideoById(id))
    }

    @GetMapping("/category/{categoryId}")
    fun getVideosByCategory(@PathVariable categoryId: Long): ResponseEntity<List<VideoDTO>> {
        return ResponseEntity.ok(videoService.getVideosByCategory(categoryId))
    }

    @GetMapping("/search")
    fun searchVideosByTitle(@RequestParam title: String): ResponseEntity<List<VideoDTO>> {
        return ResponseEntity.ok(videoService.searchVideosByTitle(title))
    }
    
    @GetMapping("/youtube-metadata")
    fun getYouTubeMetadata(@RequestParam url: String): ResponseEntity<YouTubeVideoDetails> {
        val videoId = youTubeService.extractVideoId(url)
        if (videoId == null) {
            return ResponseEntity.badRequest().build()
        }
        
        val videoDetails = youTubeService.getVideoDetails(videoId)
        if (videoDetails == null) {
            return ResponseEntity.notFound().build()
        }
        
        return ResponseEntity.ok(videoDetails)
    }

    @PostMapping
    fun createVideo(@Valid @RequestBody videoCreateDTO: VideoCreateDTO): ResponseEntity<VideoDTO> {
        val createdVideo = videoService.createVideo(videoCreateDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVideo)
    }

    @PutMapping("/{id}")
    fun updateVideo(
        @PathVariable id: Long,
        @Valid @RequestBody videoUpdateDTO: VideoUpdateDTO
    ): ResponseEntity<VideoDTO> {
        return ResponseEntity.ok(videoService.updateVideo(id, videoUpdateDTO))
    }

    @DeleteMapping("/{id}")
    fun deleteVideo(@PathVariable id: Long): ResponseEntity<Void> {
        videoService.deleteVideo(id)
        return ResponseEntity.noContent().build()
    }
} 