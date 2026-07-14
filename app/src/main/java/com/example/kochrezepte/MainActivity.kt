package com.example.kochrezepte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.kochrezepte.navigation.AppNavGraph
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.KochRezepteTheme
import com.example.kochrezepte.viewmodel.RecipeViewModel
import com.example.kochrezepte.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KochRezepteApp()
        }
    }
}

@Composable
fun KochRezepteApp() {
    KochRezepteTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
            val navController = rememberNavController()
            val recipeViewModel: RecipeViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            AppNavGraph(
                navController = navController,
                recipeViewModel = recipeViewModel,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
