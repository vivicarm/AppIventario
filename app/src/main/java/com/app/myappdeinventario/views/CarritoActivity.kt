package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.app.myappdeinventario.model.CarritoItem
import com.app.myappdeinventario.viewModel.CarritoViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeOscuroProfundo

class CarritoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                CarritoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(carritoViewModel: CarritoViewModel = viewModel()) {
    val context = LocalContext.current
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    val totalItems by carritoViewModel.totalItems.collectAsState()
    val totalPrecio by carritoViewModel.totalPrecio.collectAsState()

    LaunchedEffect(Unit) {
        // Cargar carrito desde SharedPreferences
        carritoViewModel.cargarCarritoDesdeStorage(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Carrito de Compras",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as CarritoActivity).finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            if (itemsCarrito.isNotEmpty()) {
                CheckoutBar(totalItems = totalItems, totalPrecio = totalPrecio, carritoViewModel = carritoViewModel)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            if (itemsCarrito.isEmpty()) {
                EmptyCartView()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = itemsCarrito, key = { it.producto.idProduct }) { item ->
                        CartItemCard(item, carritoViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.inventario),
            contentDescription = "Carrito vacío",
            modifier = Modifier.size(120.dp),
            alpha = 0.5f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu carrito está vacío",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Agrega algunos productos para comenzar",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        val context = LocalContext.current
        Button(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as CarritoActivity).finish()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = verdeOscuroProfundo
            )
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ir a comprar"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ir a Comprar")
        }
    }
}

@Composable
fun CartItemCard(item: CarritoItem, carritoViewModel: CarritoViewModel) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = item.producto.image.firstOrNull(),
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.inventario),
                error = painterResource(id = R.drawable.inventario)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "S/. ${String.format("%.2f", item.producto.precioV)}",
                    fontSize = 14.sp,
                    color = verdeOscuroProfundo,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Stock disponible: ${item.producto.stock}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Controles de cantidad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón de eliminar
                IconButton(
                    onClick = {
                        carritoViewModel.removerProducto(item.producto.idProduct, context)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar del carrito",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            carritoViewModel.actualizarCantidad(item.producto.idProduct, item.cantidad - 1, context)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir cantidad",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = item.cantidad.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    IconButton(
                        onClick = {
                            if (item.cantidad < item.producto.stock) {
                                carritoViewModel.actualizarCantidad(item.producto.idProduct, item.cantidad + 1, context)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar cantidad",
                            tint = verdeOscuroProfundo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subtotal
                Text(
                    text = "S/. ${String.format("%.2f", item.subtotal)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuroProfundo
                )
            }
        }
    }
}

@Composable
fun CheckoutBar(totalItems: Int, totalPrecio: Double, carritoViewModel: CarritoViewModel) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalItems producto${if (totalItems != 1) "s" else ""}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Total: S/. ${String.format("%.2f", totalPrecio)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuroProfundo
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Limpiar carrito
                        carritoViewModel.limpiarCarrito(context)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text("Limpiar Carrito")
                }

                Button(
                    onClick = {
                        // Implementar checkout
                        // Por ahora solo mostrar mensaje
                        // Toast.makeText(context, "Funcionalidad de checkout próximamente", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verdeOscuroProfundo
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Pagar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proceder al Pago")
                }
            }
        }
    }
}