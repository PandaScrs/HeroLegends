package com.pandascr.herolegends.maps

import EnemyBehavior
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pandascr.herolegends.character.Player
import com.pandascr.herolegends.R
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.Offset
import kotlin.math.max
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.min
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.pandascr.herolegends.PlayerHealthBar
import com.pandascr.herolegends.ShootingManager
import com.pandascr.herolegends.ShootingPad
import com.pandascr.herolegends.ShootingStick
import com.pandascr.herolegends.character.Enemy
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun NatureMap(player: Player, navController: NavController) {
    val blockSize = 50.dp
    val blockSizePx = with(LocalDensity.current) { blockSize.toPx() }
    val enemyBehavior = remember { EnemyBehavior(largeMap, blockSizePx) }

    var playerX by remember { mutableStateOf(2f) }
    var playerY by remember { mutableStateOf(2f) }

    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val cameraOffsetX = (screenWidthPx / 2) - (playerX * blockSizePx + blockSizePx / 2)
    val cameraOffsetY = (screenHeightPx / 2) - (playerY * blockSizePx + blockSizePx / 2)

    val enemies = remember { mutableStateListOf<Enemy>() }
    val shootingManager = remember { ShootingManager() }

    var playerDead by remember { mutableStateOf(false) }
    var playerInvincible by remember { mutableStateOf(false) }
    var playerFlicker by remember { mutableStateOf(false) }
    var lastDamageTime by remember { mutableStateOf(0L) }

    val context = LocalContext.current
    val mediaPlayerScenary = remember { MediaPlayer.create(context, R.raw.scenary_music) }
    val mediaPlayerHurt = remember { MediaPlayer.create(context, R.raw.hurt_player) }
    val mediaPlayerDeathEffect = remember { MediaPlayer.create(context, R.raw.death_sound_effect) }
    val mediaPlayerDeathMusic = remember { MediaPlayer.create(context, R.raw.death_music) }

    var isMoving by remember { mutableStateOf(false) }
    var facingRight by remember { mutableStateOf(true) }

    var playerOpacity by remember { mutableStateOf(1f) }

    // Cargar las animaciones
    val idlePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.char_idle)
            .decoderFactory(GifDecoder.Factory())
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    val walkingPainter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(R.drawable.char_walking)
            .decoderFactory(GifDecoder.Factory())
            .size(coil.size.Size.ORIGINAL)
            .build()
    )

    // Función para actualizar el estado de movimiento y dirección
    fun updateMovement(dx: Float, dy: Float) {
        isMoving = dx != 0f || dy != 0f
        if (dx != 0f) {
            facingRight = dx > 0
        }
    }

    DisposableEffect(Unit) {
        mediaPlayerScenary.isLooping = true
        mediaPlayerScenary.start()

        onDispose {
            mediaPlayerScenary.stop()
            mediaPlayerScenary.release()
            mediaPlayerHurt.release()
            mediaPlayerDeathEffect.release()
            mediaPlayerDeathMusic.release()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> mediaPlayerScenary.pause()
                Lifecycle.Event.ON_RESUME -> mediaPlayerScenary.start()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    fun applyDamageToPlayer(damageDirectionX: Float, damageDirectionY: Float, damageAmount: Int) {
        if (!playerInvincible && player.health > 0) {
            val effectiveDamage = max(0, damageAmount - player.defense)
            player.health -= effectiveDamage
            if (player.health < 0) player.health = 0

            val pushDistance = 1.3f
            playerX -= damageDirectionX * pushDistance
            playerY -= damageDirectionY * pushDistance

            playerInvincible = true
            lastDamageTime = System.currentTimeMillis()
        }
    }

    LaunchedEffect(Unit) {
        enemies.addAll(generateEnemies(largeMap, 3..5)) // Aumentar estos números genera más enemigos, pero.. cuidado (?


        while (true) {
            val deltaTime = 0.016f

            enemies.forEach { enemy ->
                enemyBehavior.moveEnemy(enemy, deltaTime, largeMap, blockSizePx)

                // Rango de ataque de los enemigos, sumado al cooldown de ataque
                if (enemyBehavior.isPlayerInRange(playerX, playerY, enemy.x, enemy.y, range = 5f)) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime > enemy.attackCooldown) {
                        // Calcular la dirección hacia el jugador
                        val dx = playerX - enemy.x
                        val dy = playerY - enemy.y
                        val length = kotlin.math.sqrt(dx * dx + dy * dy)
                        val dirX = dx / length
                        val dirY = dy / length

                        // Disparar bala del enemigo hacia el jugador
                        shootingManager.shootBullet(
                            x = enemy.x + 0.5f,
                            y = enemy.y + 0.5f,
                            directionX = dirX,
                            directionY = dirY,
                            speed = 1f,
                            damage = enemy.damage,
                            isPlayerBullet = false
                        )
                        enemy.attackCooldown = currentTime + 2000L // cooldown de 2 seg para los ataques
                    }
                }
            }

            shootingManager.updateBullets(largeMap, blockSizePx, enemies)
            shootingManager.checkPlayerHit(playerX, playerY, player) { dx, dy, damage ->
                applyDamageToPlayer(dx, dy, damage)
            }

            withFrameNanos { }
            delay(16L)
        }
    }

    LaunchedEffect(lastDamageTime) {
        if (playerInvincible) {
            val start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < 2000L) {
                playerFlicker = !playerFlicker
                playerOpacity = if (playerFlicker) 0.5f else 1f
                delay(200L)
            }
            playerFlicker = false
            playerInvincible = false
            playerOpacity = 1f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
        ) {
            PlayerHealthBar(
                player = player,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(150.dp)
                    .padding(16.dp)
            )
        }


        MapView(
            mapData = largeMap,
            blockSize = blockSize,
            playerX = playerX,
            playerY = playerY,
            cameraOffsetX = cameraOffsetX,
            cameraOffsetY = cameraOffsetY
        )

        Image(
            painter = if (isMoving) walkingPainter else idlePainter,
            contentDescription = null,
            modifier = Modifier
                .size(blockSize)
                .offset { IntOffset((playerX * blockSizePx + cameraOffsetX).toInt(), (playerY * blockSizePx + cameraOffsetY).toInt()) }
                .graphicsLayer {
                    scaleX = if (facingRight) 1f else -1f
                }
                .alpha(playerOpacity)
        )

        enemies.forEach { enemy ->
            Image(
                painter = painterResource(id = R.drawable.mushroom_enemy),
                contentDescription = null,
                modifier = Modifier
                    .size(blockSize)
                    .offset { IntOffset((enemy.x * blockSizePx + cameraOffsetX).toInt(), (enemy.y * blockSizePx + cameraOffsetY).toInt()) }
            )
        }

        shootingManager.getBullets().forEach { bullet ->
            Canvas(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (bullet.x * blockSizePx + cameraOffsetX).toInt(),
                            (bullet.y * blockSizePx + cameraOffsetY).toInt()
                        )
                    }
                    .size(20.dp, 5.dp)
            ) {
                rotate(bullet.getAngle()) {
                    drawRect(color = bullet.color)
                }
            }
        }
    }


    fun reiniciarEscenario() {
        player.health = 100
        playerDead = false
        mediaPlayerDeathMusic.stop()
        mediaPlayerScenary.start()
        navController.navigate("natureMap") {
            popUpTo("natureMap") { inclusive = true }
        }
    }

    fun volverAlMenu() {
        player.health = 100
        playerDead = false
        mediaPlayerDeathMusic.stop()
        navController.navigate("menu") {
            popUpTo("menu") { inclusive = true }
        }
    }

    if (player.health <= 0 && !playerDead) {
        playerDead = true
        mediaPlayerScenary.stop()
        mediaPlayerDeathEffect.start()
        mediaPlayerDeathMusic.start()
    }

    if (playerDead) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Moriste", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { reiniciarEscenario() }) {
                    Text("Reiniciar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { volverAlMenu() }) {
                    Text("Volver al Menú principal")
                }
            }
        }
    }

    if (enemies.isEmpty() && !playerDead) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "Proceder",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        navController.navigate("natureMap") {
                            popUpTo("natureMap") { inclusive = true }
                        }
                    },
                color = Color.White
            )
        }
    }


    if (!playerDead) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MovementStick(modifier = Modifier.size(200.dp)) { dx, dy ->
                updateMovement(dx, dy)
                val nextX = playerX + dx * player.moveSpeed
                val nextY = playerY + dy * player.moveSpeed

                val nearbyBlocks = getNearbyBlocks(nextX, nextY, largeMap)

                val playerHitboxX = Hitbox(
                    nextX * blockSizePx + blockSizePx * 0.1f,
                    playerY * blockSizePx + blockSizePx * 0.1f,
                    blockSizePx * 0.8f,
                    blockSizePx * 0.8f
                )
                val playerHitboxY = Hitbox(
                    playerX * blockSizePx + blockSizePx * 0.1f,
                    nextY * blockSizePx + blockSizePx * 0.1f,
                    blockSizePx * 0.8f,
                    blockSizePx * 0.8f
                )

                var canMoveX = true
                var canMoveY = true

                for ((col, row) in nearbyBlocks) {
                    val blockHitbox = Hitbox(col * blockSizePx, row * blockSizePx, blockSizePx, blockSizePx)
                    if (playerHitboxX.intersects(blockHitbox)) {
                        canMoveX = false
                    }
                    if (playerHitboxY.intersects(blockHitbox)) {
                        canMoveY = false
                    }
                }

                if (canMoveX) playerX = nextX
                if (canMoveY) playerY = nextY
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            ShootingPad { dx, dy ->
                shootingManager.shootBullet(
                    playerX + 0.5f, playerY + 0.5f, dx, dy, speed = 0.5f, damage = player.damage, isPlayerBullet = true
                )
            }
        }
    }

    if (!playerDead) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            ShootingPad { dx, dy ->
                shootingManager.shootBullet(
                    playerX + 0.5f, playerY + 0.5f, dx, dy, speed = 0.5f, damage = player.damage, isPlayerBullet = true
                )
            }
        }
    }
}

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

    fun generateEnemies(mapData: List<List<Int>>, enemyCount: IntRange): MutableList<Enemy> {
        val validPositions = mutableListOf<Pair<Int, Int>>()
        mapData.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == 0) { // Celda válida
                    validPositions.add(Pair(rowIndex, colIndex))
                }
            }
        }
        validPositions.shuffle()
        val selectedPositions = validPositions.take(Random.nextInt(enemyCount.first, enemyCount.last + 1))

        // Crea enemigos en esas posiciones
        return selectedPositions.map { (row, col) ->
            Enemy(health = 50, shield = 10, defense = 5, damage = 50).apply {
                x = col.toFloat()
                y = row.toFloat()
            }
        }.toMutableList()
    }

    @Composable
    fun MovementStick(modifier: Modifier = Modifier, onMove: (Float, Float) -> Unit) {
        val outerRadius = 120f // Radio del círculo grande
        val innerRadius = 40f  // Radio del círculo pequeño
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var isDragging by remember { mutableStateOf(false) }

        // Limita el movimiento del círculo pequeño dentro del círculo grande
        fun limitMovement(dx: Float, dy: Float): Pair<Float, Float> {
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
            return if (distance <= outerRadius - innerRadius) {
                dx to dy
            } else {
                val scale = (outerRadius - innerRadius) / distance
                dx * scale to dy * scale
            }
        }

        // Efecto mover al jugador de manera constante
        LaunchedEffect(isDragging) {
            while (isDragging) {
                // Calcular la velocidad según la distancia del centro (Más lejos = más rapidez)
                val distanceFromCenter = kotlin.math.sqrt(offsetX * offsetX + offsetY * offsetY)
                val speedFactor = if (distanceFromCenter < outerRadius / 2) 0.5f else 1.0f

                // Movimiento continuo basado en la dirección actual
                onMove(offsetX * speedFactor / outerRadius, offsetY * speedFactor / outerRadius)
                withFrameNanos { }
                kotlinx.coroutines.delay(16L)
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size((outerRadius * 2).dp)
                .background(Color.Transparent)
                .border(2.dp, Color.Gray.copy(alpha = 0.7f), shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            offsetX = 0f
                            offsetY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        // Calcular el movimiento limitado
                        val (limitedDx, limitedDy) = limitMovement(offsetX + dragAmount.x, offsetY + dragAmount.y)
                        // Actualizar las posiciones
                        offsetX = limitedDx
                        offsetY = limitedDy
                    }
                }
        ) {
            // Círculo pequeño (stick)
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .size((innerRadius * 2).dp)
                    .background(Color.DarkGray.copy(alpha = 0.6f), shape = CircleShape)
            )
        }
    }