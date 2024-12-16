package com.pandascr.herolegends

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(isClicked) {
        if (isClicked) {
            playSound(context, R.raw.start_sound)
            delay(500) //
            navController.navigate("loading")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isClicked = true
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.wallpaperbase),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logobase),
                contentDescription = null,
                modifier = Modifier
                    .size(350.dp)
                    .offset(y = (-70).dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pulsa para continuar",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .offset(y = -50.dp)
            )
        }
    }
}

fun playSound(context: android.content.Context, soundResId: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundResId)
    mediaPlayer.setOnCompletionListener { mp ->
        mp.release()
    }
    mediaPlayer.start()
}