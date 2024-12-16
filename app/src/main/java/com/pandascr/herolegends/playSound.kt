package com.pandascr.herolegends

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Desuso de la función playSound (directamente hecha en HomeScreen.kt, y otros archivos)

@Composable
fun playSound(resourceId: Int) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, resourceId) }
    mediaPlayer.start()
}