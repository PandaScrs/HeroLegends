package com.pandascr.herolegends

import android.app.Activity
import android.content.Intent
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            val navController = rememberNavController()
            MyApp(navController)
        }

        // Verificar si el enlace de inicio de sesión fue recibido
        handleEmailLinkIntent()
    }

    private fun handleEmailLinkIntent() {
        val auth = FirebaseAuth.getInstance()
        val intent = intent

        if (auth.isSignInWithEmailLink(intent.data.toString())) {
            val email = "user@example.com" // Obtener el correo de alguna forma (por ejemplo, SharedPreferences o del usuario)

            auth.signInWithEmailLink(email, intent.data.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("EmailLinkLogin", "Sign-in successful")
                        val user = task.result?.user
                        // Navegar a la pantalla principal o realizar alguna acción
                    } else {
                        Log.e("EmailLinkLogin", "Error signing in", task.exception)
                    }
                }
        }
    }
}

    @Composable
    fun MyApp(navController: NavHostController) {
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
                composable("loading") {
                    LoadingScreen(navController)
                }
                composable("menu") {
                    MenuScreen(navController)
                }
            }
        }
    }
