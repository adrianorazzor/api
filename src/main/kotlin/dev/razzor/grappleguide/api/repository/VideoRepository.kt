package dev.razzor.grappleguide.api.repository

import dev.razzor.grappleguide.api.model.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepository : JpaRepository<Video, Long> {
    fun findByCategoryId(categoryId: Long): List<Video>
    fun findByTitleContainingIgnoreCase(title: String): List<Video>
} 