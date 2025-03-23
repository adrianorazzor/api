package dev.razzor.grappleguide.api.repository

import dev.razzor.grappleguide.api.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByNameIgnoreCase(name: String): Category?
    fun existsByNameIgnoreCase(name: String): Boolean
} 