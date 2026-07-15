package com.example.kochrezepte.ui.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kochrezepte.ui.theme.BackgroundBlack
import com.example.kochrezepte.ui.theme.HermesOrange
import com.example.kochrezepte.ui.theme.HermesOrangeLight
import com.example.kochrezepte.ui.theme.SurfaceDarkElevated
import com.example.kochrezepte.ui.theme.TextSecondary
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kochrezepte.R




@Composable
fun OnboardingScreen(onDiscoverClick: () -> Unit) {
    Scaffold(containerColor = BackgroundBlack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                .size(320.dp)
                .clip(RoundedCornerShape(48.dp)),
            )


            Spacer(Modifier.height(32.dp))
            Text("KochRezepte", color = HermesOrange, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text("Dein digitales Kochbuch.", color = HermesOrangeLight, fontSize = 18.sp)

            Spacer(Modifier.weight(1f))

            Text(
                "Ihre Rezepte und Bilder werden nur lokal auf Ihrem Gerät gespeichert – keine Cloud-Übertragung.",
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Die App funktioniert komplett offline.",
                color = HermesOrangeLight,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onDiscoverClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkElevated),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = HermesOrange)
                Spacer(Modifier.width(8.dp))
                Text("Entdecken", color = HermesOrange, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
