package com.example.kochrezepte.data.repository

import android.content.Context
import android.net.Uri
import com.example.kochrezepte.data.local.ImageStorageManager
import com.example.kochrezepte.data.local.RecipeJsonStorage
import com.example.kochrezepte.data.model.Category
import com.example.kochrezepte.data.model.Recipe
import com.example.kochrezepte.data.model.RecipeDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tüm veri erişimi bu sınıf üzerinden yapılır. ViewModel'lar sadece
 * bu Repository'i kullanır, JSON/DataStore/dosya detaylarını bilmez.
 */
class RecipeRepository(context: Context) {

    private val jsonStorage = RecipeJsonStorage(context)
    private val imageStorage = ImageStorageManager(context)

    private val _state = MutableStateFlow(RecipeDatabase())
    val state: StateFlow<RecipeDatabase> = _state.asStateFlow()

    suspend fun initialize() {
        _state.value = jsonStorage.load()
    }

    fun resolveImageFile(fileName: String?) = imageStorage.getImageFile(fileName)

    suspend fun addOrUpdateRecipe(recipe: Recipe, newImageUri: Uri?) {
        var updated = recipe
        if (newImageUri != null) {
            imageStorage.saveImage(newImageUri)?.let { fileName ->
                // Eski resim varsa ve değiştiyse eskisini sil
                recipe.imageFileName?.let { old -> if (old != fileName) imageStorage.deleteImage(old) }
                updated = recipe.copy(imageFileName = fileName)
            }
        }
        val current = _state.value
        val exists = current.recipes.any { it.id == updated.id }
        val newList = if (exists) {
            current.recipes.map { if (it.id == updated.id) updated else it }
        } else {
            current.recipes + updated
        }
        _state.value = current.copy(recipes = newList)
        jsonStorage.save(_state.value)
    }

    suspend fun deleteRecipe(recipeId: String) {
        val current = _state.value
        val recipe = current.recipes.find { it.id == recipeId }
        recipe?.imageFileName?.let { imageStorage.deleteImage(it) }
        _state.value = current.copy(recipes = current.recipes.filterNot { it.id == recipeId })
        jsonStorage.save(_state.value)
    }

    suspend fun addOrUpdateCategory(category: Category, newImageUri: Uri?) {
        var updated = category
        if (newImageUri != null) {
            imageStorage.saveImage(newImageUri)?.let { fileName -> updated = category.copy(imageFileName = fileName) }
        }
        val current = _state.value
        val exists = current.categories.any { it.id == updated.id }
        val newList = if (exists) {
            current.categories.map { if (it.id == updated.id) updated else it }
        } else current.categories + updated
        _state.value = current.copy(categories = newList)
        jsonStorage.save(_state.value)
    }

    suspend fun deleteCategory(categoryId: String) {
        val current = _state.value
        _state.value = current.copy(
            categories = current.categories.filterNot { it.id == categoryId },
            recipes = current.recipes.map { r ->
                if (categoryId in r.categoryIds) r.copy(categoryIds = r.categoryIds - categoryId) else r
            }
        )
        jsonStorage.save(_state.value)
    }

    suspend fun clearAllData() {
        _state.value = RecipeDatabase()
        jsonStorage.save(_state.value)
        imageStorage.deleteAllImages()
    }
}
