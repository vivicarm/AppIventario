package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.myappdeinventario.R
import com.app.myappdeinventario.viewModel.AuthViewModel
import com.app.myappdeinventario.model.Usuario
import com.app.myappdeinventario.views.ui.theme.verdeOscuroProfundo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val usuario by viewModel.usuarioActual.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarioActual()
    }

    //estado de barra lateral
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //barra lateral deslizable
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent() // contenido barra lateral
        }
    ) {
       //barra inferior
        Scaffold(
            bottomBar = {
                BottomNavigationBar()
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Cada item, representa una sección de la pantalla
                item {
                    CardInicial(usuario = usuario)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    SearchBarSection(searchQuery = "") // Barra de búsqueda
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    PromotionsBanner() // Banner de "Promociones"
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    CategoriesRow() // categorías
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    ProductsGridSection() // productos
                }
            }
        }
    }
}

// Función que define el contenido del menú lateral deslizable
@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Menú Principal",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Opciones del menú
        DrawerMenuItem(icon = Icons.Default.Home, text = "Inicio")
        DrawerMenuItem(icon = Icons.Default.List, text = "Categorías")
        DrawerMenuItem(icon = Icons.Default.ShoppingCart, text = "Carrito")
        DrawerMenuItem(icon = Icons.Default.Person, text = "Perfil")

        Spacer(modifier = Modifier.weight(1f))

        DrawerMenuItem(
            icon = Icons.Default.ExitToApp,
            text = "Cerrar sesión",
            textColor = Color.Red
        )
    }
}


@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor
        )
    }
}
@Composable
fun CardInicial(usuario: Usuario?) {

    val nombre = usuario?.nombre ?: ""
    val genero = usuario?.genero ?: ""

    val avatarRes = if (genero.lowercase() == "mujer") {
        R.drawable.mujer
    } else {
        R.drawable.hombre
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.pantallainicio),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (genero.lowercase() == "mujer")
                            "Bienvenida, $nombre"
                        else
                            "Bienvenido, ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nos alegra verte de nuevo 👋",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Avatar usuario",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                )
            }
        }
    }
}

@Composable
fun SearchBarSection(searchQuery: String,) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {},
            modifier = Modifier.weight(1f),
            placeholder = { Text("Buscar...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.Gray,
                focusedLabelColor = verdeOscuroProfundo,
                cursorColor = verdeOscuroProfundo
            )
        )
    }
}


@Composable
fun PromotionsBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Promociones",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun CategoriesRow() {

    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Título de la sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Categorías",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Botón circular "+" para agregar categoría
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        val intent = Intent(context, ListarCategoriaActivity::class.java)
                        context.startActivity(intent)
                    }
                    .border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar categoría",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LazyRow: Lista horizontal con scroll
        // Muestra las tarjetas de categorías una al lado de la otra
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // items() permite crear múltiples elementos de forma dinámica
            items(4) { index ->
                CategoryCard()
            }
        }
    }
}

@Composable
fun CategoryCard() {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F0F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ProductsGridSection() {

    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Título de la sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Productos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Botón circular "+" para agregar producto
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        val intent = Intent(context, ListarProductoActivity::class.java)
                        context.startActivity(intent)
                    }
                    .border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LazyVerticalGrid: Grid vertical con scroll
        // GridCells.Fixed(2) = 2 columnas fijas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(400.dp) // Altura fija para el grid
        ) {
            // items() crea 4 tarjetas de productos
            items(4) { index ->
                ProductCard()
            }
        }
    }
}

// TARJETA DE PRODUCTO
// Card cuadrado que representa un producto individual
@Composable
fun ProductCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Mantiene proporción cuadrada (1:1)
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F0F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

// BARRA DE NAVEGACIÓN INFERIOR
// Muestra 4 íconos en la parte inferior de la pantalla
@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Ícono: Casa/Inicio
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Casa"
                )
            }
        )

        // Ícono: Cliente/Usuario
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Cliente"
                )
            }
        )

        // Ícono: Carrito de compras
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Carrito"
                )
            }
        )

        // Ícono: Ventas/Gráfico
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Ventas"
                )
            }
        )
    }
}