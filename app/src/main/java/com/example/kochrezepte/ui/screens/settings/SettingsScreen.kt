package com.example.kochrezepte.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kochrezepte.navigation.AppDestinations
import com.example.kochrezepte.ui.components.BottomNavBar
import com.example.kochrezepte.ui.components.BottomTab
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.DangerRed
import com.example.kochrezepte.ui.theme.SurfaceDark
import com.example.kochrezepte.ui.theme.TextPrimary
import com.example.kochrezepte.viewmodel.RecipeViewModel
import com.example.kochrezepte.viewmodel.SettingsViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.platform.LocalContext


@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    recipeViewModel: RecipeViewModel
) {
    val userName by settingsViewModel.userName.collectAsState()
    var nameField by remember(userName) { mutableStateOf(userName) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var importUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            recipeViewModel.exportData(uri) { success ->
                Toast.makeText(context, if (success) "Backup erfolgreich exportiert" else "Export fehlgeschlagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) importUri = uri }
    val language by settingsViewModel.language.collectAsState()

    Scaffold(
        containerColor = BackgroundBlack,
        bottomBar = {
            BottomNavBar(selected = BottomTab.SETTINGS, onTabSelected = { tab ->
                when (tab) {
                    BottomTab.HOME -> navController.navigate(AppDestinations.Home.route)
                    BottomTab.RECIPES -> navController.navigate(AppDestinations.RecipeList.withCategory())
                    BottomTab.CATEGORIES -> navController.navigate(AppDestinations.CategoryList.route)
                    BottomTab.SETTINGS -> {}
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Einstellungen", color = TextPrimary, style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(28.dp)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nameField,
                    onValueChange = { nameField = it; settingsViewModel.setUserName(it) },
                    label = { Text("Benutzername") },
                    modifier = Modifier.fillMaxWidth()
                )

                /*Box {
                    OutlinedButton(onClick = { showLanguageMenu = true }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(languageLabel(language))
                            Icon(Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }
                    DropdownMenu(expanded = showLanguageMenu, onDismissRequest = { showLanguageMenu = false }) {
                        listOf("de" to "Deutsch", "tr" to "Türkçe", "en" to "English").forEach { (code, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = {
                                settingsViewModel.setLanguage(code); showLanguageMenu = false
                            })
                        }
                    }
                }*/

                OutlinedButton(
                    onClick = { exportLauncher.launch("kochrezepte_backup_${System.currentTimeMillis()}.zip") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daten Exportieren")
                        Icon(Icons.Default.Download, contentDescription = null)
                    }
                }

                OutlinedButton(
                    onClick = { importLauncher.launch(arrayOf("application/zip")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daten Importieren")
                        Icon(Icons.Default.Upload, contentDescription = null)
                    }
                }

                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Alle Daten löschen")
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Alle Daten löschen?") },
            text = { Text("Diese Aktion kann nicht rückgängig gemacht werden.") },
            confirmButton = {
                TextButton(onClick = {
                    recipeViewModel.clearAllData()
                    showDeleteConfirm = false
                }) { Text("Löschen", color = DangerRed) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Abbrechen") } }
        )
    }

    importUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { importUri = null },
            title = { Text("Daten importieren?") },
            text = { Text("Bestehende Rezepte und Kategorien werden mit den Daten aus der Backup-Datei überschrieben.") },
            confirmButton = {
                TextButton(onClick = {
                    recipeViewModel.importData(uri) { success ->
                        Toast.makeText(
                            context,
                            if (success) "Backup erfolgreich importiert" else "Import fehlgeschlagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    importUri = null
                }) { Text("Importieren", color = DangerRed) }
            },
            dismissButton = { TextButton(onClick = { importUri = null }) { Text("Abbrechen") } }
        )
    }
}

private fun languageLabel(code: String) = when (code) {
    "tr" -> "Türkçe"
    "en" -> "English"
    else -> "Deutsch"
}
