package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

// Función en desuso, no se utiliza en el código, quise separar esta función de NatureMap.kt
// para poder deshabilitar este control cuando el jugador muriera, pero implementé esa acción sin tener que modularlo

@Composable
fun MovementStick(modifier: Modifier = Modifier, onMove: (Float, Float) -> Unit) {
    val outerRadius = 120f // Radio del círculo grande
    val innerRadius = 40f  // Radio del círculo pequeño
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Limita el movimiento del círculo pequeño dentro del círculo grande
    fun limitMovement(dx: Float, dy: Float): Pair<Float, Float> {
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        return if (distance <= outerRadius - innerRadius) {
            dx to dy
        } else {
            val scale = (outerRadius - innerRadius) / distance
            dx * scale to dy * scale
        }
    }

    // Efecto lanzado para mover el jugador de manera constante
    LaunchedEffect(isDragging) {
        while (isDragging) {
            // Calcular la velocidad según la distancia del centro
            val distanceFromCenter = kotlin.math.sqrt(offsetX * offsetX + offsetY * offsetY)
            val speedFactor = if (distanceFromCenter < outerRadius / 2) 0.5f else 1.0f

            // Movimiento continuo basado en la dirección actual
            onMove(offsetX * speedFactor / outerRadius, offsetY * speedFactor / outerRadius)

            // Controlar la frecuencia de actualización (por ejemplo, 60 veces por segundo)
            withFrameNanos { }
            kotlinx.coroutines.delay(16L)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size((outerRadius * 2).dp)
            .background(Color.Transparent) // Hacer el fondo transparente
            .border(2.dp, Color.Gray.copy(alpha = 0.7f), shape = CircleShape) // Borde con transparencia ajustable al 70%
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        // Volver al centro cuando se suelta
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()

                    // Calcular el movimiento limitado
                    val (limitedDx, limitedDy) = limitMovement(offsetX + dragAmount.x, offsetY + dragAmount.y)

                    // Actualizar las posiciones
                    offsetX = limitedDx
                    offsetY = limitedDy
                }
            }
    ) {
        // Círculo pequeño (stick)
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                .size((innerRadius * 2).dp)
                .background(Color.DarkGray.copy(alpha = 0.6f), shape = CircleShape) // Transparencia ajustable al 60%
        )
    }
}