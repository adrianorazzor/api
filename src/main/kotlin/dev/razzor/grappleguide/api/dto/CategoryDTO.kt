package dev.razzor.grappleguide.api.dto

import dev.razzor.grappleguide.api.model.Category
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryDTO(
    val id: Long?,
    val name: String,
    val description: String?
) {
    companion object {
        fun fromEntity(category: Category): CategoryDTO {
            return CategoryDTO(
                id = category.id,
                name = category.name,
                description = category.description
            )
        }
    }
}

data class CategoryCreateDTO(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must be less than 100 characters")
    val name: String,
    
    @field:Size(max = 500, message = "Description must be less than 500 characters")
    val description: String? = null
)

data class CategoryUpdateDTO(
    @field:Size(max = 100, message = "Name must be less than 100 characters")
    val name: String? = null,
    
    @field:Size(max = 500, message = "Description must be less than 500 characters")
    val description: String? = null
) 