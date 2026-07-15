package com.example.kochrezepte.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kochrezepte.R
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.kochrezepte.ui.theme.HermesOrangeDark


@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    recipeViewModel: RecipeViewModel
) {
    val userName by settingsViewModel.userName.collectAsState()
    var nameField by remember(userName) { mutableStateOf(userName) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var importUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
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
            Spacer(Modifier.height(25.dp))
            Column(
                modifier = Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(28.dp)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Datenhinweis",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Um Ihre Rezepte zu sichern, tippen Sie einfach auf „Daten Exportieren“ und speichern Sie die ZIP‑Datei auf Ihrem Smartphone.\n\n" +
                                "Wenn Sie das Gerät wechseln oder Ihre Rezepte auf ein anderes Handy übertragen möchten, schicken Sie diese ZIP‑Datei an das neue Gerät.\n\n" +
                                "Öffnen Sie dort die KochRezepte‑App, wählen Sie „Daten Importieren“ und anschließend die ZIP‑Datei aus.\n\n" +
                                "Damit werden alle Rezepte vollständig übernommen.",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(5.dp))
                    val uriHandler = LocalUriHandler.current
                    Text(
                        text = "Mehr Informationen..",
                        color = HermesOrangeDark,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/caneroktay/CNR-Kochrezepte-Android")
                        }
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Entwickelt von\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            ) {
                                append("Caner Oktay")
                            }
                        },
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "caneroktay.com",
                        color = HermesOrangeDark,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://caneroktay.com")
                        }
                    )
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

