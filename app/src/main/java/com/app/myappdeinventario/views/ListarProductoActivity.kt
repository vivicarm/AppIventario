package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.myappdeinventario.R
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.viewModel.ProductoViewModel
import com.app.myappdeinventario.views.ui.components.shimmerEffect
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro

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


// Skeleton Card Component
@Composable
fun SkeletonProductCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(12.dp)
        ) {
            // Skeleton Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Skeleton Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Skeleton Price
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Skeleton Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarProducto(productoViewModel: ProductoViewModel = viewModel()) {

    val context = LocalContext.current
    val productos by productoViewModel.productos.collectAsState()
    val cargandoLista by productoViewModel.cargandoLista.collectAsState()
    val mensaje by productoViewModel.mensajeEstado.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // Mostrar mensajes
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            productoViewModel.limpiarMensaje()
        }
    }

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
                .height(120.dp)
                .padding(top = 40.dp),
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

        // Barra de búsqueda
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

        // Filtrar productos por búsqueda
        val productosFiltrados = productos.filter {
            it.nombre.contains(searchQuery, ignoreCase = true)
        }

        // Diálogo de confirmación para eliminar
        if (mostrarDialogoEliminar && productoAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = false },
                title = { Text("Eliminar Producto") },
                text = { Text("¿Estás seguro de que deseas eliminar '${productoAEliminar?.nombre}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            productoAEliminar?.let { productoViewModel.eliminarProducto(it) }
                            mostrarDialogoEliminar = false
                            productoAEliminar = null
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEliminar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Mostrar skeleton loading o contenido
        if (cargandoLista) {
            // Mostrar skeleton loading
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(6) { // Mostrar 6 skeletons
                    SkeletonProductCard()
                }
            }
        } else if (productosFiltrados.isEmpty()) {
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
            // Mostrar productos en 2 columnas
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
                            .height(260.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            // Imagen del producto
                            if (producto.image.isNotEmpty()) {
                                AsyncImage(
                                    model = producto.image.first(),
                                    contentDescription = producto.nombre,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.inventario),
                                    error = painterResource(id = R.drawable.inventario)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.inventario),
                                    contentDescription = "Imagen por defecto",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Nombre del producto
                            Text(
                                text = producto.nombre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = verdePetróleoOscuro,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Precio
                            Text(
                                text = "S/. ${String.format("%.2f", producto.precioV)}",
                                fontSize = 14.sp,
                                color = verdeAzuladoMedio,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Stock
                            Text(
                                text = "Stock: ${producto.stock}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            // Botones de acción
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Botón Editar
                                IconButton(
                                    onClick = {
                                        if (producto.idProduct.isNotBlank()) {
                                            val intent = Intent(context, EditarProductoActivity::class.java).apply {
                                                putExtra("productoId", producto.idProduct)
                                                putExtra("productoNombre", producto.nombre)
                                                putExtra("productoDescripcion", producto.descripcion)
                                                putExtra("productoPrecioC", producto.precioC)
                                                putExtra("productoPrecioV", producto.precioV)
                                                putExtra("productoPrecioPromocion", producto.precioPromocion)
                                                putExtra("productoStock", producto.stock)
                                                putExtra("productoFecha", producto.fechaCreacion)
                                                putExtra("productoImagenes", producto.image.toTypedArray())
                                                putExtra("productoCategoria", producto.idCategory)
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Producto no válido para editar", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .background(verdeAzuladoMedio.copy(alpha = 0.2f), CircleShape)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = verdeAzuladoMedio,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Botón Eliminar
                                IconButton(
                                    onClick = {
                                        if (producto.idProduct.isNotBlank()) {
                                            productoAEliminar = producto
                                            mostrarDialogoEliminar = true
                                        } else {
                                            Toast.makeText(context, "Producto no válido para eliminar", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .background(Color.Red.copy(alpha = 0.2f), CircleShape)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
