package com.pandascr.herolegends

import com.pandascr.herolegends.character.Enemy
import com.pandascr.herolegends.character.Player
import com.pandascr.herolegends.entities.Bullet
import kotlin.math.max

class ShootingManager {
    private val bullets = mutableListOf<Bullet>()

    fun shootBullet(x: Float, y: Float, directionX: Float, directionY: Float, speed: Float = 0.1f, damage: Int, isPlayerBullet: Boolean) {
        bullets.add(Bullet(x, y, directionX, directionY, speed, damage, isPlayerBullet))
    }

    fun updateBullets(mapData: List<List<Int>>, blockSize: Float, enemies: MutableList<Enemy>) {
        val bulletsToRemove = mutableListOf<Bullet>()
        val enemiesToRemove = mutableListOf<Enemy>()

        bullets.forEach { bullet ->
            if (bullet.updatePosition(mapData)) {
                bulletsToRemove.add(bullet)
            }

            if (bullet.isPlayerBullet) {
                enemies.forEach { enemy ->
                    if (bullet.collidesWith(enemy)) {
                        bullet.damageEnemy(enemy)
                        bulletsToRemove.add(bullet)
                        if (enemy.health <= 0) {
                            enemiesToRemove.add(enemy)
                        }
                    }
                }
            }
        }

        bullets.removeAll(bulletsToRemove)
        enemies.removeAll(enemiesToRemove)
    }

    fun Bullet.damageEnemy(enemy: Enemy) {
        val effectiveDamage = damage - enemy.defense
        enemy.health -= max(0, effectiveDamage)
    }

    fun checkPlayerHit(playerX: Float, playerY: Float, player: Player, onPlayerDamaged: (Float, Float, Int) -> Unit) {
        val bulletsToRemove = mutableListOf<Bullet>()

        for (bullet in bullets) {
            if (!bullet.isPlayerBullet) {
                // Bala enemiga
                if (bullet.collidesWithPlayer(playerX, playerY)) {
                    // Direccion del golpe
                    val dx = bullet.directionX
                    val dy = bullet.directionY
                    onPlayerDamaged(dx, dy, bullet.damage)
                    bulletsToRemove.add(bullet)
                }
            }
        }

        bullets.removeAll(bulletsToRemove)
    }

    fun getBullets(): List<Bullet> = bullets
}