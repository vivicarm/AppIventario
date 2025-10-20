package com.app.myappdeinventario.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import com.app.myappdeinventario.views.ui.components.shimmerEffect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.app.myappdeinventario.model.Categoria
import com.app.myappdeinventario.viewModel.CategoriaViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro

class ListarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                 ListarCategory()
                }
            }
        }
    }


// Skeleton Card Component
@Composable
fun SkeletonCategoryCard() {
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
            modifier = Modifier.padding(12.dp)
        ) {
            // Skeleton Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Skeleton Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Skeleton Description
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarCategory(categoriaViewModel: CategoriaViewModel = viewModel()) {

    val context = LocalContext.current
    val categorias by categoriaViewModel.categorias.collectAsState()
    val cargandoLista by categoriaViewModel.cargandoLista.collectAsState()
    val mensaje by categoriaViewModel.mensajeEstado.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var categoriaAEliminar by remember { mutableStateOf<Categoria?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // Mostrar mensajes
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            categoriaViewModel.limpiarMensaje()
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
                        text = "CATEGORIAS",
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    IconButton(
                        onClick = {
                            val intent = Intent(context, AgregarCategoriaActivity::class.java)
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
            placeholder = { Text("Buscar categorias...") },
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

        // Filtrar categorías por búsqueda
        val categoriasFiltradas = categorias.filter {
            it.nombreCategory.contains(searchQuery, ignoreCase = true)
        }

        // Diálogo de confirmación para eliminar
        if (mostrarDialogoEliminar && categoriaAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = false },
                title = { Text("Eliminar Categoría") },
                text = { Text("¿Estás seguro de que deseas eliminar '${categoriaAEliminar?.nombreCategory}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            categoriaAEliminar?.let { categoriaViewModel.eliminarCategoria(it) }
                            mostrarDialogoEliminar = false
                            categoriaAEliminar = null
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
                    SkeletonCategoryCard()
                }
            }
        } else if (categoriasFiltradas.isEmpty()) {
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
                            text = "Ningúna categoria disponible",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = verdePetróleoOscuro
                        )

                        Button(
                            onClick = {
                                val intent = Intent(context, AgregarCategoriaActivity::class.java)
                                context.startActivity(intent)
                            }
                        ) {
                            Text("Agregar categoria", color = Color.White)
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.category),
                        contentDescription = "Imagen categorias",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(start = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        } else {
            // Mostrar categorías en 2 columnas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(categoriasFiltradas) { categoria ->
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            // Imagen de la categoría
                            AsyncImage(
                                model = categoria.imagenCategoria,
                                contentDescription = categoria.nombreCategory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.category),
                                error = painterResource(id = R.drawable.category)
                            )

                            // Nombre de la categoría
                            Text(
                                text = categoria.nombreCategory,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = verdePetróleoOscuro,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Descripción
                            Text(
                                text = categoria.descripcionCategory,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Botones de acción
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Botón Editar
                                IconButton(
                                    onClick = {
                                        if (categoria.idCategory.isNotBlank()) {
                                            val intent = Intent(context, EditarCategoriaActivity::class.java).apply {
                                                putExtra("categoriaId", categoria.idCategory)
                                                putExtra("categoriaNombre", categoria.nombreCategory)
                                                putExtra("categoriaDescripcion", categoria.descripcionCategory)
                                                putExtra("categoriaImagen", categoria.imagenCategoria)
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Categoría no válida para editar", Toast.LENGTH_SHORT).show()
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
                                        if (categoria.idCategory.isNotBlank()) {
                                            categoriaAEliminar = categoria
                                            mostrarDialogoEliminar = true
                                        } else {
                                            Toast.makeText(context, "Categoría no válida para eliminar", Toast.LENGTH_SHORT).show()
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

