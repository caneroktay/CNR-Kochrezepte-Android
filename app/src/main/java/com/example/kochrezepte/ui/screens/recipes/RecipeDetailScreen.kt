package com.example.kochrezepte.ui.screens.recipes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.kochrezepte.data.model.Ingredient
import com.example.kochrezepte.data.model.Recipe
import com.example.kochrezepte.ui.components.BottomNavBar
import com.example.kochrezepte.ui.components.BottomTab
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.DangerRed
import com.example.kochrezepte.ui.theme.HermesOrange
import com.example.kochrezepte.ui.theme.SuccessGreen
import com.example.kochrezepte.ui.theme.SurfaceDark
import com.example.kochrezepte.ui.theme.SurfaceDarkElevated
import com.example.kochrezepte.ui.theme.TextPrimary
import com.example.kochrezepte.ui.theme.TextSecondary
import com.example.kochrezepte.viewmodel.RecipeViewModel
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme

@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    recipeId: String?
) {
    val db by recipeViewModel.state.collectAsState()
    val existing = remember(db, recipeId) { db.recipes.find { it.id == recipeId } }
    val isNew = existing == null

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var timeMinutes by remember { mutableStateOf(existing?.timeMinutes ?: 30) }
    var preparation by remember { mutableStateOf(existing?.preparation ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var links by remember { mutableStateOf(existing?.links ?: emptyList()) }
    var selectedCategoryIds by remember { mutableStateOf(existing?.categoryIds ?: emptyList()) }
    var ingredients by remember { mutableStateOf(existing?.ingredients ?: emptyList()) }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val existingImageFile = recipeViewModel.resolveImageFile(existing?.imageFileName)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) pickedImageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { _ -> /* TODO: ImageStorageManager  */ }

    fun handleSave() {
        val recipe = (existing ?: Recipe(title = title)).copy(
            title = title,
            categoryIds = selectedCategoryIds,
            description = description,
            timeMinutes = timeMinutes,
            ingredients = ingredients,
            preparation = preparation,
            note = note,
            links = links.filter { it.isNotBlank() }
        )
        recipeViewModel.saveRecipe(recipe, pickedImageUri)
        navController.popBackStack()
    }

    Scaffold(
        containerColor = BackgroundBlack,
        // bottomBar = { BottomNavBar(selected = BottomTab.RECIPES, onTabSelected = {}) },
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(5.dp, 60.dp, 15.dp, 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    Text("Zurück")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isNew) {
                        Button(
                            onClick = {
                                existing?.let { recipeViewModel.deleteRecipe(it.id) }
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated)
                        ) { Text("Löschen", color = DangerRed) }
                    }
                    Button(
                        onClick = { handleSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) { Text("Speichern", color = Color.Black) }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Text(if (isNew) "Neu Rezept..." else "Rezept bearbeiten", color = TextSecondary) }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    val displayUri = pickedImageUri
                    when {
                        displayUri != null -> AsyncImage(
                            model = displayUri, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                        existingImageFile != null -> AsyncImage(
                            model = existingImageFile, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                        else -> IconButton(onClick = { showImageSourceDialog = true }) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Foto hinzufügen", tint = HermesOrange)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Rezept Title") }, modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Box {
                    OutlinedButton(onClick = { showCategoryMenu = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (selectedCategoryIds.isEmpty()) "Kategorien"
                            else db.categories.filter { it.id in selectedCategoryIds }.joinToString { it.name }
                        )
                    }
                    DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                        db.categories.forEach { category ->
                            val checked = category.id in selectedCategoryIds
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                leadingIcon = { Checkbox(checked = checked, onCheckedChange = null) },
                                onClick = {
                                    selectedCategoryIds = if (checked) selectedCategoryIds - category.id
                                    else selectedCategoryIds + category.id
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Rezeptbeschreibung") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(16.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Zeit (Minuten)", color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$timeMinutes", color = TextPrimary)
                        Column {
                            IconButton(onClick = { timeMinutes++ }, modifier = Modifier.size(44.dp)) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = TextSecondary)
                            }
                            IconButton(onClick = { if (timeMinutes > 0) timeMinutes-- }, modifier = Modifier.size(44.dp)) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
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
                    Text("Zutaten", color = TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { ingredients = ingredients + Ingredient(name = "") }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = HermesOrange)
                    }
                }
            }

            items(ingredients) { ingredient ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ingredient.amount,
                        onValueChange = { value ->
                            ingredients = ingredients.map { if (it.id == ingredient.id) it.copy(amount = value) else it }
                        },
                        label = { Text("Menge") }, modifier = Modifier.weight(0.35f)
                    )
                    OutlinedTextField(
                        value = ingredient.name,
                        onValueChange = { value ->
                            ingredients = ingredients.map { if (it.id == ingredient.id) it.copy(name = value) else it }
                        },
                        label = { Text("Zutat") }, modifier = Modifier.weight(0.65f)
                    )
                    IconButton(onClick = { ingredients = ingredients.filterNot { it.id == ingredient.id } }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = DangerRed)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = preparation, onValueChange = { preparation = it },
                    label = { Text("Zubereitung") },
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Note") }, modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Links", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { links = links + "" }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = HermesOrange)
                    }
                }
            }

            itemsIndexed(links) { index, link ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = link,
                        onValueChange = { value -> links = links.toMutableList().also { it[index] = value } },
                        label = { Text("z. B. https://youtube.com/...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { links = links.toMutableList().also { it.removeAt(index) } }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = DangerRed)
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Foto auswählen") },
            text = { Text("Woher möchten Sie das Bild hinzufügen?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) { Text("Galerie") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraLauncher.launch(null)
                }) { Text("Kamera") }
            }
        )
    }
}
