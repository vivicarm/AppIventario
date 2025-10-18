package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.myappdeinventario.R
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeOscuroProfundo
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro

class AgregarVariacionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                CrearVariacion()

                }
            }
        }
    }


@Composable
fun CrearVariacion() {

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(scrollState)    //Habilita desplazamiento vertical
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp) //corto
                .padding(top = 40.dp), // hacia abajo
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.pantallainicio),
                    contentDescription = "Fondo de bienvenida",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Text(
                    text = "Crea una variación",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }

        // Divisiones tipo botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Producto", "Especificaciones", "Variaciones").forEach { titulo ->
                TextButton(onClick = {
                    val intent = when (titulo) {
                        "Especificaciones" -> Intent(
                            context,
                            AgregarEspecificacionActivity::class.java
                        )

                        "Variaciones" -> Intent(context, AgregarVariacionActivity::class.java)
                        else -> Intent(context, AgregaProductoActivity::class.java)
                    }
                    intent.putExtra("titulo", titulo)
                    context.startActivity(intent)
                }) {
                    Text(titulo, color = verdeOscuroProfundo, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Ningúna variación disponible",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = verdePetróleoOscuro
                    )

                    Button(
                        onClick = {
                            val intent = Intent(context, AgregaProductoActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Agregar Variación", color = Color.White)
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.inventario),
                    contentDescription = "Imagen productos",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(start = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
