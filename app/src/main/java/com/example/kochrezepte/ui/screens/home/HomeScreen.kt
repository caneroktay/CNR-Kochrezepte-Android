package com.example.kochrezepte.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.kochrezepte.navigation.AppDestinations
import com.example.kochrezepte.ui.components.BottomNavBar
import com.example.kochrezepte.ui.components.BottomTab
import com.example.kochrezepte.ui.components.RecipeListItem
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.SurfaceDark
import com.example.kochrezepte.ui.theme.SurfaceDarkElevated
import com.example.kochrezepte.ui.theme.TextPrimary
import com.example.kochrezepte.ui.theme.TextSecondary
import com.example.kochrezepte.viewmodel.RecipeViewModel
import com.example.kochrezepte.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    settingsViewModel: SettingsViewModel
) {
    val db by recipeViewModel.state.collectAsState()
    val userName by settingsViewModel.userName.collectAsState()

    Scaffold(
        containerColor = BackgroundBlack,
        bottomBar = {
            BottomNavBar(selected = BottomTab.HOME, onTabSelected = { tab ->
                when (tab) {
                    BottomTab.HOME -> {}
                    BottomTab.RECIPES -> navController.navigate(AppDestinations.RecipeList.withCategory())
                    BottomTab.CATEGORIES -> navController.navigate(AppDestinations.CategoryList.route)
                    BottomTab.SETTINGS -> navController.navigate(AppDestinations.Settings.route)
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(55.dp).clip(CircleShape).background(SurfaceDarkElevated),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (userName.isBlank()) "KochRezepte" else "$userName" + "s Rezepte",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(20.dp))
            Text("Kategorien", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(db.categories) { category ->
                    val imageFile = recipeViewModel.resolveImageFile(category.imageFileName)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .clickable {
                                navController.navigate(AppDestinations.RecipeList.withCategory(category.id))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageFile != null) {
                            AsyncImage(
                                model = imageFile,
                                contentDescription = category.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(category.name, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Lieblingsrezepte…", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(db.recipes.filter { it.isFavorite }) { recipe ->
                    RecipeListItem(
                        title = recipe.title,
                        subtitle = "Favorit",
                        imageFile = recipeViewModel.resolveImageFile(recipe.imageFileName),
                        isFavorite = recipe.isFavorite,
                        onToggleFavorite = { recipeViewModel.toggleFavorite(recipe.id) },
                        onClick = { navController.navigate(AppDestinations.RecipeView.withRecipe(recipe.id)) },

                    )
                }
            }
        }
    }
}
