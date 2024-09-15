package com.pandascr.herolegends

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun playSound(resourceId: Int) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, resourceId) }
    mediaPlayer.start()
}