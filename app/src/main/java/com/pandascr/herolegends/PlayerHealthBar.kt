package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pandascr.herolegends.character.Player

@Composable
fun PlayerHealthBar(player: Player, modifier: Modifier = Modifier) {
    val maxHealth = 100f
    val healthFraction = player.health / maxHealth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(healthFraction)
                .background(Color.Green)
        )
    }
}