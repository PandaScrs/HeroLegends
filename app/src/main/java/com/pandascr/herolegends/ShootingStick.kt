package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

// Función en desuso, esta era parte para hacer que el jugador disparara automáticamente en dirección al enemigo
// pero preferí hacer disparo manual, está más chistoso

@Composable
fun ShootingStick(onShoot: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color.Red, shape = CircleShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White, shape = CircleShape)
                .clickable { onShoot() }
        )
    }
}