package com.example.kochrezepte.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kochrezepte.data.model.Category
import com.example.kochrezepte.data.model.Recipe
import com.example.kochrezepte.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(application)
    val state = repository.state

    init {
        viewModelScope.launch { repository.initialize() }
    }

    fun resolveImageFile(fileName: String?) = repository.resolveImageFile(fileName)

    fun saveRecipe(recipe: Recipe, newImageUri: Uri? = null) {
        viewModelScope.launch { repository.addOrUpdateRecipe(recipe, newImageUri) }
    }

    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch { repository.deleteRecipe(recipeId) }
    }

    fun toggleFavorite(recipeId: String) {
        val current = state.value.recipes.find { it.id == recipeId } ?: return
        viewModelScope.launch {
            repository.addOrUpdateRecipe(current.copy(isFavorite = !current.isFavorite), null)
        }
    }

    fun saveCategory(category: Category, newImageUri: Uri? = null) {
        viewModelScope.launch { repository.addOrUpdateCategory(category, newImageUri) }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch { repository.deleteCategory(categoryId) }
    }

    fun clearAllData() {
        viewModelScope.launch { repository.clearAllData() }
    }
}
