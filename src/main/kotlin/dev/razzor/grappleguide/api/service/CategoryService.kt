package dev.razzor.grappleguide.api.service

import dev.razzor.grappleguide.api.dto.CategoryCreateDTO
import dev.razzor.grappleguide.api.dto.CategoryDTO
import dev.razzor.grappleguide.api.dto.CategoryUpdateDTO
import dev.razzor.grappleguide.api.exception.ResourceAlreadyExistsException
import dev.razzor.grappleguide.api.exception.ResourceNotFoundException
import dev.razzor.grappleguide.api.model.Category
import dev.razzor.grappleguide.api.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<CategoryDTO> {
        return categoryRepository.findAll().map { CategoryDTO.fromEntity(it) }
    }

    fun getCategoryById(id: Long): CategoryDTO {
        val category = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }
        return CategoryDTO.fromEntity(category)
    }

    @Transactional
    fun createCategory(categoryCreateDTO: CategoryCreateDTO): CategoryDTO {
        if (categoryRepository.existsByNameIgnoreCase(categoryCreateDTO.name)) {
            throw ResourceAlreadyExistsException("Category with name '${categoryCreateDTO.name}' already exists")
        }

        val category = Category(
            name = categoryCreateDTO.name,
            description = categoryCreateDTO.description
        )

        val savedCategory = categoryRepository.save(category)
        return CategoryDTO.fromEntity(savedCategory)
    }

    @Transactional
    fun updateCategory(id: Long, categoryUpdateDTO: CategoryUpdateDTO): CategoryDTO {
        val existingCategory = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }

        // Check if name is being changed and if it already exists
        if (categoryUpdateDTO.name != null && 
            categoryUpdateDTO.name != existingCategory.name && 
            categoryRepository.existsByNameIgnoreCase(categoryUpdateDTO.name)) {
            throw ResourceAlreadyExistsException("Category with name '${categoryUpdateDTO.name}' already exists")
        }

        // Update the fields if they are not null
        categoryUpdateDTO.name?.let { existingCategory.name = it }
        categoryUpdateDTO.description?.let { existingCategory.description = it }

        val updatedCategory = categoryRepository.save(existingCategory)
        return CategoryDTO.fromEntity(updatedCategory)
    }

    @Transactional
    fun deleteCategory(id: Long) {
        if (!categoryRepository.existsById(id)) {
            throw ResourceNotFoundException("Category not found with id: $id")
        }
        categoryRepository.deleteById(id)
    }
} 