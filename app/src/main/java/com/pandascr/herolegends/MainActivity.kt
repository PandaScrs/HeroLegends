package com.pandascr.herolegends

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pandascr.herolegends.character.Player
import com.pandascr.herolegends.maps.NatureMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.setLoggingEnabled(true)
        FirebaseApp.initializeApp(this)

        // Crear la instancia del Player, y establecer sus estadísticas
        val player = Player(
            health = 100,
            shield = 50,
            defense = 20,
            damage = 30,
            moveSpeed = 0.2f
        )

        setContent {
            val navController = rememberNavController()
            MyApp(navController, player)
        }

        // Verificar si el enlace de inicio de sesión fue recibido
        handleEmailLinkIntent()
    }

    private fun handleEmailLinkIntent() {
        val auth = FirebaseAuth.getInstance()
        val intent = intent

        if (auth.isSignInWithEmailLink(intent.data.toString())) {
            val email = "user@example.com"

            auth.signInWithEmailLink(email, intent.data.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("EmailLinkLogin", "Sign-in successful")
                        val user = task.result?.user
                    } else {
                        Log.e("EmailLinkLogin", "Error signing in", task.exception)
                    }
                }
        }
    }
}

@Composable
fun MyApp(navController: NavHostController, player: Player) {
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        ViewCompat.getWindowInsetsController(view)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                TextAnimationScreen(
                    texts = listOf("PandaScr, presenta:", "Una obra de simulación de juego", "Hero Legends."),
                    onAnimationEnd = {
                        navController.navigate("home")
                    }
                )
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("login") {
                LoginScreen(navController)
            }
            composable("loading") {
                LoadingScreen(navController)
            }
            composable("menu") {
                MenuScreen(navController)
            }
            composable("natureMap") {
                NatureMap(player = player, navController = navController)
            }
        }
    }
}
