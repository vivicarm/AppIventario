package com.app.myappdeinventario.views


import android.content.Intent
import com.app.myappdeinventario.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.myappdeinventario.views.ui.theme.*
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme

class AppInventario : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                AppInventario(modifier = Modifier)
            }
        }
    }
}

@Composable
fun AppInventario( modifier: Modifier = Modifier){
    val context = LocalContext.current

    // Box principal
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.imagewelcome),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Recorta la imagen para llenar
        )

        // contenido encima texto y botones
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra verticalmente
        ) {
            // Título
            Text(
                text = "BIENVENIDOS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = verdeOscuroProfundo,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "Gestiona tu inventario de manera eficiente",
                fontSize = 16.sp,
                color = grisVerdoso,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(300.dp))

            // Botón Login
            Button(
                onClick = {
                    val intent = Intent(context, IniciarSesionActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verdeAzuladoMedio
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto registrarse
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    fontSize = 16.sp,
                    color = verdeOscuroProfundo,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = {
                        val intent = Intent(context, RegistroActivity::class.java)
                        context.startActivity(intent)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 16.sp,
                        color = verdeOscuroProfundo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}