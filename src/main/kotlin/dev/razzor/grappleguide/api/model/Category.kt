package dev.razzor.grappleguide.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @field:NotBlank
    @field:Size(max = 100)
    @Column(nullable = false, unique = true)
    var name: String,
    
    @field:Size(max = 500)
    @Column(length = 500)
    var description: String? = null,
    
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val videos: MutableSet<Video> = mutableSetOf()
) 