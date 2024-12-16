package com.pandascr.herolegends

import android.app.Activity
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun MenuScreen(navController: NavController) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.menu_background_sound) }
    if (!isInPreview) {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(R.drawable.menu_wallpaper)
                    .decoderFactory(GifDecoder.Factory())
                    .size(Size.ORIGINAL)
                    .build()
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.logobase),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(250.dp)
                .offset(y = (-50).dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jugar",
                fontSize = 48.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 34.dp, bottom = 5.dp)
                    .clickable {
                        mediaPlayer.stop()
                        navController.navigate("natureMap")
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Salir",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 22.dp)
                    .clickable {
                        FirebaseAuth.getInstance().signOut()
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        (context as Activity).finish()
                    }
            )
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun MenuScreenPreview() {
    val navController = rememberNavController()
    MenuScreen(navController = navController)
}