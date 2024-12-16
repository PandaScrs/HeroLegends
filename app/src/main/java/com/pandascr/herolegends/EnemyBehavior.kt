import com.pandascr.herolegends.character.Enemy
import com.pandascr.herolegends.maps.Hitbox
import com.pandascr.herolegends.getNearbyBlocks
import kotlin.math.sqrt
import kotlin.random.Random

class EnemyBehavior(
    private val mapData: List<List<Int>>,
    private val blockSizePx: Float
) {
    // Movimiento aleatorio para los enemigos
    fun moveEnemy(enemy: Enemy, deltaTime: Float, mapData: List<List<Int>>, blockSizePx: Float) {
        val currentTime = System.currentTimeMillis()

        // Cambiar dirección si ha pasado el cooldown
        if (currentTime > enemy.changeDirectionCooldown) {
            enemy.directionX = listOf(-1f, 0f, 1f).random()
            enemy.directionY = listOf(-1f, 0f, 1f).random()
            enemy.changeDirectionCooldown = currentTime + 1000L // Cambiar dirección en 1 segundo
        }

        // Calcular nuevo desplazamiento basado en la dirección
        val dx = enemy.directionX * enemy.moveSpeed * deltaTime
        val dy = enemy.directionY * enemy.moveSpeed * deltaTime

        val nextX = enemy.x + dx
        val nextY = enemy.y + dy

        // Verificar colisiones con bloques sólidos
        val colX = (nextX).toInt()
        val rowX = (enemy.y).toInt()
        val colY = (enemy.x).toInt()
        val rowY = (nextY).toInt()

        if (colX in mapData[0].indices && rowX in mapData.indices && mapData[rowX][colX] == 0) {
            enemy.x = nextX
        }
        if (colY in mapData[0].indices && rowY in mapData.indices && mapData[rowY][colY] == 0) {
            enemy.y = nextY
        }
    }

    private fun canMoveTo(x: Float, y: Float): Boolean {
        val col = x.toInt()
        val row = y.toInt()
        return col in mapData[0].indices && row in mapData.indices && mapData[row][col] == 0
    }

    // Detectar si el jugador está en rango de ataque
    fun isPlayerInRange(playerX: Float, playerY: Float, enemyX: Float, enemyY: Float, range: Float): Boolean {
        val dx = playerX - enemyX
        val dy = playerY - enemyY
        val distance = sqrt(dx * dx + dy * dy)
        return distance <= range
    }
}