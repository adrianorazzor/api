package dev.razzor.grappleguide.api.controller

import dev.razzor.grappleguide.api.dto.CategoryCreateDTO
import dev.razzor.grappleguide.api.dto.CategoryDTO
import dev.razzor.grappleguide.api.dto.CategoryUpdateDTO
import dev.razzor.grappleguide.api.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryDTO>> {
        return ResponseEntity.ok(categoryService.getAllCategories())
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<CategoryDTO> {
        return ResponseEntity.ok(categoryService.getCategoryById(id))
    }

    @PostMapping
    fun createCategory(@Valid @RequestBody categoryCreateDTO: CategoryCreateDTO): ResponseEntity<CategoryDTO> {
        val createdCategory = categoryService.createCategory(categoryCreateDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory)
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @Valid @RequestBody categoryUpdateDTO: CategoryUpdateDTO
    ): ResponseEntity<CategoryDTO> {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryUpdateDTO))
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
} 