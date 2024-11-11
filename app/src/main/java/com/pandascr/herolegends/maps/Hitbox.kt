package com.pandascr.herolegends.maps

data class Hitbox(val x: Float, val y: Float, val width: Float, val height: Float) {
    fun intersects(other: Hitbox): Boolean {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y
    }
}