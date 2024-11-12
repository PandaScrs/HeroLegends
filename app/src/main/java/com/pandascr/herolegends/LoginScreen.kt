package com.pandascr.herolegends

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    var showRegisterFields by remember { mutableStateOf(false) }
    var showRecoverPasswordFields by remember { mutableStateOf(false) }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.loginscreenbackground)
                    .size(Size.ORIGINAL)
                    .build()
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = when {
                showRegisterFields -> "Registro"
                showRecoverPasswordFields -> "Recuperar Contraseña"
                else -> "Inicio de Sesión"
            },
            color = Color.White,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = when {
                showRegisterFields -> "Por favor, complete los campos para registrarse."
                showRecoverPasswordFields -> "Por favor, ingrese su correo electrónico para recuperar la contraseña."
                else -> "No se ha detectado ninguna cuenta, por favor, inicie sesión."
            },
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        if (showRegisterFields) {
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Nombre de usuario", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(start = 16.dp)
                    .background(Color.Transparent),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo electrónico", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(start = 16.dp)
                .background(Color.Transparent),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!showRecoverPasswordFields) {
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Contraseña", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(start = 16.dp)
                    .background(Color.Transparent),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (showRegisterFields) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            isLoading.value = true
                            registerUser(email.value, password.value, username.value, context, navController, isLoading, errorMessage)
                        },
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text("Registrarse")
                    }
                    Button(
                        onClick = { showRegisterFields = false },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Text("Cancelar Registro")
                    }
                }
            } else if (showRecoverPasswordFields) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            isLoading.value = true
                            recoverPassword(email.value, password.value, context, navController, isLoading, errorMessage)
                        },
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text("Enviar correo de Recuperación")
                    }
                    Button(
                        onClick = { showRecoverPasswordFields = false },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            isLoading.value = true
                            loginUser(email.value, password.value, context, navController, isLoading, errorMessage)
                        },
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text("Iniciar Sesión")
                    }
                    Button(
                        onClick = { showRecoverPasswordFields = true },
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text("Recuperar Contraseña")
                    }
                    Button(
                        onClick = { showRegisterFields = true },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Text("Registrarse")
                    }
                }
            }
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (errorMessage.value != null) {
            Text(
                text = errorMessage.value!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

fun registerUser(email: String, password: String, username: String, context: android.content.Context, navController: NavController, isLoading: MutableState<Boolean>, errorMessage: MutableState<String?>) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    db.collection("users").whereEqualTo("username", username).get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            navController.navigate("menu")
                        } else {
                            errorMessage.value = "Error al registrar: ${task.exception?.message}"
                            Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                isLoading.value = false
                errorMessage.value = "El nombre de usuario ya está en uso."
                Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener { e ->
            isLoading.value = false
            errorMessage.value = "Error al verificar el nombre de usuario: ${e.message}"
            Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
        }
}

fun loginUser(email: String, password: String, context: android.content.Context, navController: NavController, isLoading: MutableState<Boolean>, errorMessage: MutableState<String?>) {
    FirebaseAuth.getInstance()
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                navController.navigate("menu")
            } else {
                val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                val errorMessageText = when (errorCode) {
                    "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico no es válido."
                    "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
                    "ERROR_USER_NOT_FOUND" -> "No hay ningún usuario registrado con este correo electrónico."
                    "ERROR_USER_DISABLED" -> "La cuenta ha sido deshabilitada."
                    "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos fallidos. Inténtalo de nuevo más tarde."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "El inicio de sesión con correo electrónico y contraseña no está habilitado."
                    else -> "Error desconocido: ${task.exception?.message}"
                }
                errorMessage.value = errorMessageText
                Toast.makeText(context, errorMessageText, Toast.LENGTH_LONG).show()
            }
        }
}

fun recoverPassword(email: String, newPassword: String, context: android.content.Context, navController: NavController, isLoading: MutableState<Boolean>, errorMessage: MutableState<String?>) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    db.collection("users").whereEqualTo("email", email).get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val userId = documents.documents[0].id
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Correo de recuperación enviado.", Toast.LENGTH_LONG).show()
                            navController.navigate("login")
                        } else {
                            errorMessage.value = "Error al enviar correo de recuperación: ${task.exception?.message}"
                            Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                isLoading.value = false
                errorMessage.value = "No se encontró una cuenta con este correo electrónico."
                Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener { e ->
            isLoading.value = false
            errorMessage.value = "Error al verificar el correo electrónico: ${e.message}"
            Toast.makeText(context, errorMessage.value, Toast.LENGTH_LONG).show()
        }
}