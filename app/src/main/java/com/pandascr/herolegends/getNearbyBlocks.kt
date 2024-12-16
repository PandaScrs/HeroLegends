package com.pandascr.herolegends

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

// Función en desuso, no se utiliza en el código, fue una prueba de modular el código (quería crear diversos mapas) está colocado directamente en NatureMap.kt

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