package com.pandascr.herolegends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TextAnimationScreen(texts: List<String>, onAnimationEnd: () -> Unit) {
    var currentTextIndex by remember { mutableStateOf(0) }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(currentTextIndex) {
        if (currentTextIndex < texts.size) {
            isVisible = true
            delay(2000)
            isVisible = false
            delay(2000)
            currentTextIndex++
        } else {
            onAnimationEnd()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentTextIndex < texts.size) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Text(
                    text = texts[currentTextIndex],
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}