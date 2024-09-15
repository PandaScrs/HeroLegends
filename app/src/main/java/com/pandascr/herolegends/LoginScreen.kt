package com.pandascr.herolegends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .zIndex(-1f) // Asegura que el fondo esté detrás de todo el contenido
    )

    // Contenido de la pantalla de inicio de sesión
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val isLoading = remember { mutableStateOf(false) }
        val errorMessage = remember { mutableStateOf("") }

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading.value = true
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            navController.navigate("menu") // Navega al menú si el inicio de sesión es exitoso
                        } else {
                            val errorCode = (task.exception as FirebaseAuthException).errorCode
                            errorMessage.value = when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico no es válido."
                                "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
                                "ERROR_USER_NOT_FOUND" -> "No hay ningún usuario registrado con este correo electrónico."
                                "ERROR_USER_DISABLED" -> "La cuenta ha sido deshabilitada."
                                "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos fallidos. Inténtalo de nuevo más tarde."
                                "ERROR_OPERATION_NOT_ALLOWED" -> "El inicio de sesión con correo electrónico y contraseña no está habilitado."
                                else -> "Error desconocido: ${task.exception?.message}"
                            }
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}