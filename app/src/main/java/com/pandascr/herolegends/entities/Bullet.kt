package com.pandascr.herolegends.entities

import androidx.compose.ui.graphics.Color
import com.pandascr.herolegends.character.Enemy
import com.pandascr.herolegends.maps.Hitbox
import kotlin.math.atan2

class Bullet(
    var x: Float,
    var y: Float,
    val directionX: Float,
    val directionY: Float,
    val speed: Float,
    val damage: Int,
    val isPlayerBullet: Boolean
) {
    val color: Color = if (isPlayerBullet) Color.Green else Color.Red

    fun updatePosition(mapData: List<List<Int>>): Boolean {
        x += directionX * speed
        y += directionY * speed

        val col = x.toInt()
        val row = y.toInt()

        if (row !in mapData.indices || col !in mapData[0].indices) {
            // Bala fuera del mapa
            return true
        }

        if (mapData[row][col] == 1) {
            // Colisión con bloque sólido
            return true
        }

        return false
    }

    fun collidesWith(enemy: Enemy): Boolean {
        val dx = (x + 0.5f) - (enemy.x + 0.5f)
        val dy = (y + 0.5f) - (enemy.y + 0.5f)
        return kotlin.math.sqrt(dx * dx + dy * dy) < 1.0f
    }

    fun getAngle(): Float {
        val angleRadians = atan2(directionY, directionX)
        val angleDegrees = Math.toDegrees(angleRadians.toDouble()).toFloat() - 90f
        return angleDegrees
    }

    fun collidesWithEnemy(enemy: Enemy, blockSize: Float): Boolean {
        val dx = (x + 0.5f) - (enemy.x + 0.5f)
        val dy = (y + 0.5f) - (enemy.y + 0.5f)
        return kotlin.math.sqrt(dx * dx + dy * dy) < 0.5f
    }

    fun collidesWithPlayer(playerX: Float, playerY: Float): Boolean {
        val dx = (x + 0.5f) - (playerX + 0.5f)
        val dy = (y + 0.5f) - (playerY + 0.5f)
        return kotlin.math.sqrt(dx * dx + dy * dy) < 1.5f
    }
}