package com.example.kochrezepte.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kochrezepte.ui.screens.categories.CategoryListScreen
import com.example.kochrezepte.ui.screens.home.HomeScreen
import com.example.kochrezepte.ui.screens.onboarding.OnboardingScreen
import com.example.kochrezepte.ui.screens.recipes.RecipeDetailScreen
import com.example.kochrezepte.ui.screens.recipes.RecipeListScreen
import com.example.kochrezepte.ui.screens.settings.SettingsScreen
import com.example.kochrezepte.viewmodel.RecipeViewModel
import com.example.kochrezepte.viewmodel.SettingsViewModel
import com.example.kochrezepte.ui.screens.recipes.RecipeViewScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    settingsViewModel: SettingsViewModel,
    startDestination: String = AppDestinations.Onboarding.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(AppDestinations.Onboarding.route) {
            OnboardingScreen(
                onDiscoverClick = {
                    navController.navigate(AppDestinations.Home.route) {
                        popUpTo(AppDestinations.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.Home.route) {
            HomeScreen(
                navController = navController,
                recipeViewModel = recipeViewModel,
                settingsViewModel = settingsViewModel
            )
        }

        composable(AppDestinations.CategoryList.route) {
            CategoryListScreen(navController = navController, recipeViewModel = recipeViewModel)
        }

        composable(
            route = AppDestinations.RecipeList.route,
            arguments = listOf(navArgument("categoryId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.takeIf { it.isNotBlank() }
            RecipeListScreen(
                navController = navController,
                recipeViewModel = recipeViewModel,
                filterCategoryId = categoryId
            )
        }

        composable(
            route = AppDestinations.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.takeIf { it.isNotBlank() }
            RecipeDetailScreen(
                navController = navController,
                recipeViewModel = recipeViewModel,
                recipeId = recipeId
            )
        }

        composable(
            route = AppDestinations.RecipeView.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            RecipeViewScreen(
                navController = navController,
                recipeViewModel = recipeViewModel,
                recipeId = recipeId
            )
        }

        composable(AppDestinations.Settings.route) {
            SettingsScreen(
                navController = navController,
                settingsViewModel = settingsViewModel,
                recipeViewModel = recipeViewModel
            )
        }
    }
}
