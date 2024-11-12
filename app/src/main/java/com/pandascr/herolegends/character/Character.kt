package com.pandascr.herolegends.character

import kotlin.math.roundToInt

open class Character(
    var health: Int,
    var shield: Int,
    var defense: Int,
    var damage: Int
) {
    fun takeDamage(incomingDamage: Int) {
        val reducedDamage = (incomingDamage * (1 - defense / 100.0)).roundToInt()
        if (shield > 0) {
            shield -= reducedDamage
            if (shield < 0) {
                health += shield
                shield = 0
            }
        } else {
            health -= reducedDamage
        }
    }

    fun regenerateShield(amount: Int) {
        shield += amount
    }
}