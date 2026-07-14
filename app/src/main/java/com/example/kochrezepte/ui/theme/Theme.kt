package com.example.kochrezepte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KochRezepteColorScheme = darkColorScheme(
    primary = HermesOrange,
    onPrimary = TextOnOrange,
    secondary = HermesOrangeLight,
    background = BackgroundBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    outline = BorderSubtle
)

// Uygulama sadece koyu temayı destekler (mockuplara göre tasarlandı)
@Composable
fun KochRezepteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KochRezepteColorScheme,
        typography = KochRezepteTypography,
        content = content
    )
}
