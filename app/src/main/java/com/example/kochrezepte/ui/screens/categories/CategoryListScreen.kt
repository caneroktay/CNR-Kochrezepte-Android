package com.example.kochrezepte.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.kochrezepte.data.model.Category
import com.example.kochrezepte.navigation.AppDestinations
import com.example.kochrezepte.ui.components.BottomNavBar
import com.example.kochrezepte.ui.components.BottomTab
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.DangerRed
import com.example.kochrezepte.ui.theme.HermesOrange
import com.example.kochrezepte.ui.theme.SurfaceDark
import com.example.kochrezepte.ui.theme.SurfaceDarkElevated
import com.example.kochrezepte.ui.theme.TextPrimary
import com.example.kochrezepte.ui.theme.TextSecondary
import com.example.kochrezepte.viewmodel.RecipeViewModel
import java.io.File

@Composable
fun CategoryListScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val db by recipeViewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }

    val filtered = db.categories.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = BackgroundBlack,
        bottomBar = {
            BottomNavBar(selected = BottomTab.CATEGORIES, onTabSelected = { tab ->
                when (tab) {
                    BottomTab.HOME -> navController.navigate(AppDestinations.Home.route)
                    BottomTab.RECIPES -> navController.navigate(AppDestinations.RecipeList.withCategory())
                    BottomTab.CATEGORIES -> {}
                    BottomTab.SETTINGS -> navController.navigate(AppDestinations.Settings.route)
                }
            })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceDarkElevated),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) }
                    Spacer(Modifier.width(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kategorien", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = HermesOrange)
                        Text("Neu", color = HermesOrange)
                    }
                }
                Spacer(Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered) { category ->
                        val recipeCount = db.recipes.count { category.id in it.categoryIds }
                        CategoryRow(
                            title = category.name,
                            subtitle = "$recipeCount Rezept(e)",
                            imageFile = recipeViewModel.resolveImageFile(category.imageFileName),
                            onClick = { navController.navigate(AppDestinations.RecipeList.withCategory(category.id)) },
                            onEditClick = { editingCategory = category },
                            onDeleteClick = { deletingCategory = category }
                        )
                    }
                }
            }

            if (showAddDialog) {
                CategoryDialog(
                    existing = null,
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name ->
                        recipeViewModel.saveCategory(Category(name = name))
                        showAddDialog = false
                    }
                )
            }

            editingCategory?.let { category ->
                CategoryDialog(
                    existing = category,
                    onDismiss = { editingCategory = null },
                    onConfirm = { name ->
                        recipeViewModel.saveCategory(category.copy(name = name))
                        editingCategory = null
                    }
                )
            }

            deletingCategory?.let { category ->
                AlertDialog(
                    onDismissRequest = { deletingCategory = null },
                    title = { Text("Kategorie löschen?") },
                    text = { Text("\"${category.name}\" wird gelöscht. Zugeordnete Rezepte bleiben erhalten, verlieren aber diese Kategorie.") },
                    confirmButton = {
                        TextButton(onClick = {
                            recipeViewModel.deleteCategory(category.id)
                            deletingCategory = null
                        }) { Text("Löschen", color = DangerRed) }
                    },
                    dismissButton = { TextButton(onClick = { deletingCategory = null }) { Text("Abbrechen") } }
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    title: String,
    subtitle: String,
    imageFile: File?,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(SurfaceDark)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SurfaceDarkElevated),
            contentAlignment = Alignment.Center
        ) {
            if (imageFile != null) {
                AsyncImage(
                    model = imageFile,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.RamenDining, contentDescription = null, tint = TextSecondary)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = HermesOrange)
        }

        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = TextSecondary)
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = DangerRed)
        }
    }
}

@Composable
private fun CategoryDialog(existing: Category?, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Neue Kategorie" else "Kategorie bearbeiten") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Kategorie Name") })
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}