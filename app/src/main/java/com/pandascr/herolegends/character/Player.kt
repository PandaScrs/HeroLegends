package com.pandascr.herolegends.character

class Player(
    health: Int,
    shield: Int,
    defense: Int,
    damage: Int,
    var moveSpeed: Float
) : Character(health, shield, defense, damage) {
    // nohacenada
}