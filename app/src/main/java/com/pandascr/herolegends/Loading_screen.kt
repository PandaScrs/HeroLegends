package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        delay(500)
        if (user != null) {
            navController.navigate("menu") // Redirigir al Menú Principal
        } else {
            delay(1000)
            navController.navigate("login") // Redirigir al login
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(" ", modifier = Modifier.align(Alignment.Center))
    }
}