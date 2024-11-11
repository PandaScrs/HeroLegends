package com.pandascr.herolegends.maps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pandascr.herolegends.character.Player
import com.pandascr.herolegends.R
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.Offset
import kotlin.math.max
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.min
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize


@Composable
fun NatureMap(player: Player) {
    val blockSize = 50.dp
    val blockSizePx = with(LocalDensity.current) { blockSize.toPx() }

    var playerX by remember { mutableStateOf(2f) }
    var playerY by remember { mutableStateOf(2f) }

    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    // Calcular el desplazamiento de la cámara para centrar al jugador
    val cameraOffsetX = (screenWidthPx / 2) - (playerX * blockSizePx + blockSizePx / 2)
    val cameraOffsetY = (screenHeightPx / 2) - (playerY * blockSizePx + blockSizePx / 2)

    Box(modifier = Modifier.fillMaxSize()) {
        // Dibujar el mapa
        MapView(
            mapData = largeMap,
            blockSize = blockSize,
            playerX = playerX,
            playerY = playerY,
            cameraOffsetX = cameraOffsetX,
            cameraOffsetY = cameraOffsetY
        )

        // Dibujar al jugador en su posición
        Image(
            painter = painterResource(id = R.drawable.player_asset),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(blockSize)
                .offset {
                    IntOffset(
                        (playerX * blockSizePx + cameraOffsetX).toInt(),
                        (playerY * blockSizePx + cameraOffsetY).toInt()
                    )
                }
        )

        // Stick de movimiento
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MovementStick(modifier = Modifier.size(200.dp)) { dx, dy ->
                val nextX = playerX + dx * player.moveSpeed
                val nextY = playerY + dy * player.moveSpeed

                // Optimizar la detección de colisiones
                val nearbyBlocks = getNearbyBlocks(nextX, nextY, largeMap)

                // Hitbox del jugador
                val playerHitboxX = Hitbox(
                    nextX * blockSizePx + blockSizePx * 0.1f,
                    playerY * blockSizePx + blockSizePx * 0.1f,
                    blockSizePx * 0.8f,
                    blockSizePx * 0.8f
                )
                val playerHitboxY = Hitbox(
                    playerX * blockSizePx + blockSizePx * 0.1f,
                    nextY * blockSizePx + blockSizePx * 0.1f,
                    blockSizePx * 0.8f,
                    blockSizePx * 0.8f
                )

                var canMoveX = true
                var canMoveY = true

                // Detectar colisiones solo con bloques cercanos
                for ((col, row) in nearbyBlocks) {
                    val blockHitbox = Hitbox(col * blockSizePx, row * blockSizePx, blockSizePx, blockSizePx)
                    if (playerHitboxX.intersects(blockHitbox)) {
                        canMoveX = false
                    }
                    if (playerHitboxY.intersects(blockHitbox)) {
                        canMoveY = false
                    }
                }

                if (canMoveX) playerX = nextX
                if (canMoveY) playerY = nextY
            }
        }
    }
}

@Composable
fun MapView(
    mapData: List<List<Int>>,
    blockSize: Dp,
    playerX: Float,
    playerY: Float,
    cameraOffsetX: Float,
    cameraOffsetY: Float
) {
    val blockSizePx = with(LocalDensity.current) { blockSize.toPx() }
    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val mapWidth = mapData[0].size
    val mapHeight = mapData.size

    val collisionBlockImage = ImageBitmap.imageResource(id = R.drawable.collision_block)
    val floorBlockImage = ImageBitmap.imageResource(id = R.drawable.floor_block)

    val visibleCols = (0 until mapWidth).filter { col ->
        val x = col * blockSizePx + cameraOffsetX
        x + blockSizePx > 0f && x < screenWidthPx
    }

    val visibleRows = (0 until mapHeight).filter { row ->
        val y = row * blockSizePx + cameraOffsetY
        y + blockSizePx > 0f && y < screenHeightPx
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (row in visibleRows) {
            for (col in visibleCols) {
                val blockType = mapData[row][col]
                val image = if (blockType == 1) collisionBlockImage else floorBlockImage

                val destRect = android.graphics.RectF(
                    col * blockSizePx + cameraOffsetX,
                    row * blockSizePx + cameraOffsetY,
                    col * blockSizePx + cameraOffsetX + blockSizePx,
                    row * blockSizePx + cameraOffsetY + blockSizePx
                )

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawBitmap(
                        image.asAndroidBitmap(),
                        null,
                        destRect,
                        null
                    )
                }
            }
        }
    }
}

fun getNearbyBlocks(playerX: Float, playerY: Float, mapData: List<List<Int>>): List<Pair<Int, Int>> {
    val positions = mutableListOf<Pair<Int, Int>>()
    val minCol = max(0, floor(playerX - 1).toInt())
    val maxCol = min(mapData[0].size - 1, ceil(playerX + 1).toInt())
    val minRow = max(0, floor(playerY - 1).toInt())
    val maxRow = min(mapData.size - 1, ceil(playerY + 1).toInt())

    for (row in minRow..maxRow) {
        for (col in minCol..maxCol) {
            if (mapData[row][col] == 1) {
                positions.add(Pair(col, row))
            }
        }
    }
    return positions
}

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
