package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun ShootingPad(modifier: Modifier = Modifier, onShoot: (Float, Float) -> Unit) {
    val outerRadius = 120f
    val innerRadius = 40f
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    fun limitMovement(dx: Float, dy: Float): Pair<Float, Float> {
        val distance = sqrt(dx * dx + dy * dy)
        return if (distance <= outerRadius - innerRadius) {
            dx to dy
        } else {
            val scale = (outerRadius - innerRadius) / distance
            dx * scale to dy * scale
        }
    }

    LaunchedEffect(isDragging) {
        while (isDragging) {
            val length = sqrt(offsetX * offsetX + offsetY * offsetY)
            if (length > 0f) {
                val dirX = offsetX / length
                val dirY = offsetY / length
                onShoot(dirX, dirY)
            }
            kotlinx.coroutines.delay(200L)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size((outerRadius * 2).dp)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val (limitedDx, limitedDy) = limitMovement(offsetX + dragAmount.x, offsetY + dragAmount.y)
                    offsetX = limitedDx
                    offsetY = limitedDy
                }
            }
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                .size((innerRadius * 2).dp)
                .background(Color.DarkGray.copy(alpha = 0.6f), shape = CircleShape)
        )
    }
}