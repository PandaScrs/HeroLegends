package com.pandascr.herolegends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Función en desuso, no se utiliza en el código, fue una prueba de modular el código (quería crear diversos mapas) está colocado directamente en NatureMap.kt

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