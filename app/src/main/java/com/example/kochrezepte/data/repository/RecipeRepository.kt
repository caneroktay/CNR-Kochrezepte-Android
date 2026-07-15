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
import com.example.kochrezepte.data.local.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(context: Context) {

    private val jsonStorage = RecipeJsonStorage(context)
    private val imageStorage = ImageStorageManager(context)
    private val backupManager = BackupManager(context)
    private val _state = MutableStateFlow(RecipeDatabase())
    val state: StateFlow<RecipeDatabase> = _state.asStateFlow()

    suspend fun initialize() {
        _state.value = jsonStorage.load()
    }

    fun resolveImageFile(fileName: String?) = imageStorage.getImageFile(fileName)

    fun createCameraCaptureUri(): Uri = imageStorage.createCameraCaptureUri()

    suspend fun exportBackup(destinationUri: Uri): Boolean = backupManager.exportBackup(destinationUri)

    suspend fun importBackup(sourceUri: Uri): Boolean {
        val success = backupManager.importBackup(sourceUri)
        if (success) _state.value = jsonStorage.load()
        return success
    }

    suspend fun copyImage(uri: Uri): String? = withContext(Dispatchers.IO) {
        imageStorage.saveImage(uri)
    }

    suspend fun addOrUpdateRecipe(recipe: Recipe) {
        val current = _state.value
        val previous = current.recipes.find { it.id == recipe.id }
        previous?.imageFileNames?.forEach { old ->
            if (old !in recipe.imageFileNames) imageStorage.deleteImage(old)
        }

        val exists = current.recipes.any { it.id == recipe.id }
        val newList = if (exists) {
            current.recipes.map { if (it.id == recipe.id) recipe else it }
        } else {
            current.recipes + recipe
        }
        _state.value = current.copy(recipes = newList)
        jsonStorage.save(_state.value)
    }

    suspend fun deleteRecipe(recipeId: String) {
        val current = _state.value
        val recipe = current.recipes.find { it.id == recipeId }
        val filesToDelete = recipe?.imageFileNames?.ifEmpty { listOfNotNull(recipe.imageFileName) } ?: emptyList()
        filesToDelete.forEach { imageStorage.deleteImage(it) }
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
