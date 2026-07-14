package com.example.kochrezepte.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.kochrezepte.ui.theme.HermesOrange
import com.example.kochrezepte.ui.theme.SurfaceDarkTranslucent
import com.example.kochrezepte.ui.theme.TextOnOrange
import com.example.kochrezepte.ui.theme.TextSecondary

enum class BottomTab(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    RECIPES(Icons.Default.Restaurant),
    CATEGORIES(Icons.AutoMirrored.Filled.List),
    SETTINGS(Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    selected: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 45.dp, top = 5.dp, 45.dp, 32.dp)
            .background(SurfaceDarkTranslucent, RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(72.dp)
                    .background(if (isSelected) HermesOrange else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onTabSelected(tab) }) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.name,
                        tint = if (isSelected) TextOnOrange else TextSecondary
                    )
                }
            }
        }
    }
}
