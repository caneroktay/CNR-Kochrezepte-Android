package com.example.kochrezepte.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.kochrezepte.navigation.AppDestinations
import com.example.kochrezepte.ui.theme.*
import com.example.kochrezepte.viewmodel.RecipeViewModel

@Composable
fun RecipeViewScreen(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    recipeId: String?
) {
    val db by recipeViewModel.state.collectAsState()
    val recipe = remember(db, recipeId) { db.recipes.find { it.id == recipeId } }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var fullscreenIndex by remember { mutableStateOf<Int?>(null) }

    if (recipe == null) {
        navController.popBackStack()
        return
    }

    val imageFiles = remember(recipe) {
        val fileNames = recipe.imageFileNames.ifEmpty { listOfNotNull(recipe.imageFileName) }
        fileNames.mapNotNull { recipeViewModel.resolveImageFile(it) }
    }
    val categoryNames = recipe.categoryIds.mapNotNull { id -> db.categories.find { it.id == id }?.name }

    Scaffold(
        containerColor = BackgroundBlack,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextPrimary)
                    Text("Zurück", color = TextPrimary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (imageFiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(imageFiles) { index, file ->
                            Box(
                                modifier = Modifier
                                    .size(width = 260.dp, height = 220.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(SurfaceDark)
                                    .clickable { fullscreenIndex = index }
                            ) {
                                AsyncImage(
                                    model = file,
                                    contentDescription = recipe.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        recipe.title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { recipeViewModel.toggleFavorite(recipe.id) }) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = HermesOrange
                        )
                    }
                }
            }

            if (categoryNames.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categoryNames.forEach { name ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SurfaceDarkElevated)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(name, color = HermesOrange, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            if (recipe.timeMinutes > 0) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary)
                        Text("${recipe.timeMinutes} Min.", color = TextSecondary)
                    }
                }
            }

            if (recipe.description.isNotBlank()) {
                item {
                    Text(recipe.description, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (recipe.ingredients.isNotEmpty()) {
                item {
                    Text("Zutaten", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                }
                items(recipe.ingredients) { ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ingredient.name, color = TextPrimary)
                        if (ingredient.amount.isNotBlank()) {
                            Text(ingredient.amount, color = TextSecondary)
                        }
                    }
                }
            }

            if (recipe.preparation.isNotBlank()) {
                item {
                    Text("Zubereitung", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(recipe.preparation, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (recipe.note.isNotBlank()) {
                item {
                    Text("Notiz", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(recipe.note, color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (recipe.links.isNotEmpty()) {
                item {
                    Text("Links", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                }
                items(recipe.links) { link ->
                    val uriHandler = LocalUriHandler.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .clickable { uriHandler.openUri(link) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = HermesOrange)
                        Text(
                            link,
                            color = HermesOrange,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.navigate(AppDestinations.RecipeDetail.withRecipe(recipe.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = HermesOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = TextOnOrange)
                        Spacer(Modifier.width(6.dp))
                        Text("Bearbeiten", color = TextOnOrange)
                    }
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed)
                        Spacer(Modifier.width(6.dp))
                        Text("Löschen")
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Rezept löschen?") },
            text = { Text("Diese Aktion kann nicht rückgängig gemacht werden.") },
            confirmButton = {
                TextButton(onClick = {
                    recipeViewModel.deleteRecipe(recipe.id)
                    showDeleteConfirm = false
                    navController.popBackStack()
                }) { Text("Löschen", color = DangerRed) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Abbrechen") } }
        )
    }

    fullscreenIndex?.let { startIndex ->
        Dialog(
            onDismissRequest = { fullscreenIndex = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val pagerState = rememberPagerState(initialPage = startIndex) { imageFiles.size }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    AsyncImage(
                        model = imageFiles[page],
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { fullscreenIndex = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Color.White)
                }
                if (imageFiles.size > 1) {
                    Text(
                        "${pagerState.currentPage + 1} / ${imageFiles.size}",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}