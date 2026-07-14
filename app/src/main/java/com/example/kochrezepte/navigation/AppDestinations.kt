package com.example.kochrezepte.navigation

sealed class AppDestinations(val route: String) {
    data object Onboarding : AppDestinations("onboarding")
    data object Home : AppDestinations("home")
    data object CategoryList : AppDestinations("category_list")

    data object RecipeList : AppDestinations("recipe_list?categoryId={categoryId}") {
        fun withCategory(categoryId: String? = null) = "recipe_list?categoryId=${categoryId ?: ""}"
    }

    data object RecipeDetail : AppDestinations("recipe_detail?recipeId={recipeId}") {
        fun withRecipe(recipeId: String? = null) = "recipe_detail?recipeId=${recipeId ?: ""}"
    }

    data object RecipeView : AppDestinations("recipe_view/{recipeId}") {
        fun withRecipe(recipeId: String) = "recipe_view/$recipeId"
    }
    data object Settings : AppDestinations("settings")
}
