package com.example.kochrezepte.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Ingredient(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: String = "" // "200 g", "2 adet" gibi serbest metin
)

@Serializable
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    // Kategori kapak resmi - context.filesDir/images içindeki dosya ADI (tam yol değil!)
    val imageFileName: String? = null
)

@Serializable
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val categoryIds: List<String> = emptyList(),
    val description: String = "",
    val timeMinutes: Int = 0,
    val ingredients: List<Ingredient> = emptyList(),
    val preparation: String = "",
    val note: String = "",
    val links: List<String> = emptyList(),
    val imageFileName: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class RecipeDatabase(
    val categories: List<Category> = emptyList(),
    val recipes: List<Recipe> = emptyList()
)
