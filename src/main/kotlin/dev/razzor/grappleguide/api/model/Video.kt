package dev.razzor.grappleguide.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "videos")
data class Video(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @field:NotBlank
    @field:Size(max = 200)
    @Column(nullable = false)
    var title: String,
    
    @field:Size(max = 1000)
    @Column(length = 1000)
    var description: String? = null,
    
    @Column(nullable = false)
    var url: String,
    
    @Column(name = "duration_seconds")
    var durationSeconds: Int? = null,
    
    @Column(name = "video_type")
    @Enumerated(EnumType.STRING)
    var videoType: VideoType = VideoType.OTHER,
    
    @Column(name = "video_id")
    var videoId: String? = null,
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    var category: Category? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    @PreUpdate
    fun setVideoTypeAndId() {
        videoType = VideoType.fromUrl(url)
        videoId = when (videoType) {
            VideoType.YOUTUBE -> {
                val regex = Regex("""(?:youtube\.com\/(?:[^\/\n\s]+\/\S+\/|(?:v|e(?:mbed)?)\/|\S*?[?&]v=)|youtu\.be\/)([a-zA-Z0-9_-]{11})""")
                regex.find(url)?.groupValues?.get(1)
            }
            VideoType.VIMEO -> {
                val regex = Regex("""vimeo\.com(?:\/channels\/\w+)?\/(\d+)""")
                regex.find(url)?.groupValues?.get(1)
            }
            else -> null
        }
    }
} 