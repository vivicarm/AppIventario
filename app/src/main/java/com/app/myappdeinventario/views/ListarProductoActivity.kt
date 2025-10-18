package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import com.app.myappdeinventario.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.viewModel.ProductoViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListarProductoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                ListarProducto()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarProducto(productoViewModel: ProductoViewModel = viewModel()) {

    val context = LocalContext.current
    val uiState by productoViewModel.productos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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

                // Contenido encima de la imagen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "PRODUCTOS",
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    IconButton(
                        onClick = {
                            val intent = Intent(context, AgregaProductoActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .background(
                                verdeAzuladoMedio.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    verdeAzuladoMedio.copy(alpha = 0.8f),
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        )
                    }

                }
            }
        }
        // barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar productos...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = verdeAzuladoMedio
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = verdeAzuladoMedio,
                unfocusedBorderColor = verdeAzuladoMedio.copy(alpha = 0.4f),
                cursorColor = verdeAzuladoMedio
            )
        )

// filtrar productos por búsqueda
        val productosFiltrados = uiState.filter {
            it.nombre.contains(searchQuery, ignoreCase = true)
        }

//  mostrar lista de productos o mensaje vacío
        if (productosFiltrados.isEmpty()) {
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
                            text = "Ningún producto disponible",
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
                            Text("Agregar producto", color = Color.White)
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
        } else {
            // ✅ Mostrar productos en 2 columnas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(productosFiltrados) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            // Imagen del producto (usa Coil si las imágenes vienen de URL Firebase)
                            producto.image?.let { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Imagen de ${producto.nombre}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            } ?: Image(
                                painter = painterResource(id = R.drawable.inventario),
                                contentDescription = "Imagen por defecto",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                text = producto.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = verdePetróleoOscuro,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Text(
                                text = "S/. ${producto.precioV}",
                                color = verdeAzuladoMedio,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
