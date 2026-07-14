package com.example.kochrezepte.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kochrezepte.navigation.AppDestinations
import com.example.kochrezepte.ui.components.BottomNavBar
import com.example.kochrezepte.ui.components.BottomTab
import com.example.kochrezepte.ui.components.RecipeListItem
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.HermesOrange
import com.example.kochrezepte.ui.theme.SurfaceDarkElevated
import com.example.kochrezepte.ui.theme.TextPrimary
import com.example.kochrezepte.ui.theme.TextSecondary
import com.example.kochrezepte.viewmodel.RecipeViewModel

@Composable
fun RecipeListScreen(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    filterCategoryId: String?

) {
    val db by recipeViewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(filterCategoryId) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val filtered = db.recipes.filter { recipe ->
        val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryId == null || selectedCategoryId in recipe.categoryIds
        matchesSearch && matchesCategory
    }

    val screenTitle = selectedCategoryId
        ?.let { id -> db.categories.find { it.id == id }?.name }
        ?: "Meine KochRezepte"

    Scaffold(
        containerColor = BackgroundBlack,
        bottomBar = {
            BottomNavBar(selected = BottomTab.RECIPES, onTabSelected = { tab ->
                when (tab) {
                    BottomTab.HOME -> navController.navigate(AppDestinations.Home.route)
                    BottomTab.RECIPES -> {}
                    BottomTab.CATEGORIES -> navController.navigate(AppDestinations.CategoryList.route)
                    BottomTab.SETTINGS -> navController.navigate(AppDestinations.Settings.route)
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(55.dp).clip(CircleShape).background(SurfaceDarkElevated),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Suchen...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                )

                Box {
                    OutlinedButton(onClick = { showCategoryMenu = true }, shape = RoundedCornerShape(50)) {
                        Icon(imageVector = Icons.Default.ArrowDropDown , contentDescription = null, tint = HermesOrange, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Kategorien")
                    }
                    DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                        DropdownMenuItem(text = { Text("Alle") }, onClick = {
                            selectedCategoryId = null; showCategoryMenu = false
                        })
                        db.categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                selectedCategoryId = category.id; showCategoryMenu = false
                            })
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(screenTitle, color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { navController.navigate(AppDestinations.RecipeDetail.withRecipe()) }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = HermesOrange)
                    Text("Neu", color = HermesOrange)
                }
            }
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered) { recipe ->
                    val categoryName = recipe.categoryIds.firstOrNull()
                        ?.let { id -> db.categories.find { it.id == id }?.name } ?: "Rezept"
                    RecipeListItem(
                        title = recipe.title,
                        subtitle = categoryName,
                        imageFile = recipeViewModel.resolveImageFile(recipe.imageFileName),
                        isFavorite = recipe.isFavorite,
                        onToggleFavorite = { recipeViewModel.toggleFavorite(recipe.id) },
                        onClick = { navController.navigate(AppDestinations.RecipeView.withRecipe(recipe.id)) }
                    )
                }
            }
        }
    }
}
