package com.pandascr.herolegends.character

class Enemy(
    health: Int,
    shield: Int,
    defense: Int,
    damage: Int
) : Character(health, shield, defense, damage) {
    var x: Float = 0f
    var y: Float = 0f
    var moveSpeed: Float = 4.5f
    var attackCooldown: Long = 0L
    var directionX: Float = 0f
    var directionY: Float = 0f
    var changeDirectionCooldown: Long = 0L
}