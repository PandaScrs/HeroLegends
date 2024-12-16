package com.pandascr.herolegends

import com.pandascr.herolegends.character.Enemy

// Función en desuso, no se utiliza en el código, fue parte de la primera versión, esto está actualizado en NatureMap.kt

fun generateEnemies(mapData: List<List<Int>>, enemyCount: IntRange): MutableList<Enemy> {
    val validPositions = mutableListOf<Pair<Int, Int>>()

    // Encuentra todas las posiciones válidas (celdas con 0)
    mapData.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, cell ->
            if (cell == 0) { // Celda válida
                validPositions.add(Pair(rowIndex, colIndex))
            }
        }
    }

    // Return an empty list for now, you can modify this to generate actual enemies
    return mutableListOf()
}