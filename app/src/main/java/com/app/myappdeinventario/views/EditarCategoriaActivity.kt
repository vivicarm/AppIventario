package com.app.myappdeinventario.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.app.myappdeinventario.R
import com.app.myappdeinventario.model.Categoria
import com.app.myappdeinventario.viewModel.CategoriaViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro

class EditarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val categoriaId = intent.getStringExtra("categoriaId") ?: ""
        val categoriaNombre = intent.getStringExtra("categoriaNombre") ?: ""
        val categoriaDescripcion = intent.getStringExtra("categoriaDescripcion") ?: ""
        val categoriaImagen = intent.getStringExtra("categoriaImagen") ?: ""

        setContent {
            MyAppDeInventarioTheme {
                EditarCategoriaScreen(
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre,
                    categoriaDescripcion = categoriaDescripcion,
                    categoriaImagen = categoriaImagen
                )
            }
        }
    }
}

@Composable
fun EditarCategoriaScreen(
    categoriaId: String,
    categoriaNombre: String,
    categoriaDescripcion: String,
    categoriaImagen: String,
    viewModel: CategoriaViewModel = viewModel()
) {
    val context = LocalContext.current
    val mensaje by viewModel.mensajeEstado.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var nombre by remember { mutableStateOf(categoriaNombre) }
    var descripcion by remember { mutableStateOf(categoriaDescripcion) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagenActual by remember { mutableStateOf(categoriaImagen) }

    // Launcher para abrir galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // Launcher para solicitar permiso
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show()
        }
    }

    // Mostrar mensaje de ViewModel
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
            
            // Si se actualizó correctamente, volver a la lista
            if (it.contains("actualizada correctamente")) {
                val intent = Intent(context, ListarCategoriaActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
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
                    contentDescription = "Fondo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, ListarCategoriaActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = "Editar Categoría",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen seleccionada o actual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Nueva imagen",
                        modifier = Modifier
                            .size(130.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (imagenActual.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imagenActual),
                        contentDescription = "Imagen actual",
                        modifier = Modifier
                            .size(130.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen", color = Color.Gray, modifier = Modifier.padding(30.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón seleccionar imagen
                IconButton(onClick = {
                    val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        permisoLauncher.launch(permiso)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Cambiar imagen",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de categoría") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = verdePetróleoOscuro,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = verdeAzuladoMedio,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = verdeAzuladoMedio,
                unfocusedLabelColor = Color.Gray,
                cursorColor = verdeAzuladoMedio
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = verdePetróleoOscuro,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = verdeAzuladoMedio,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = verdeAzuladoMedio,
                unfocusedLabelColor = Color.Gray,
                cursorColor = verdeAzuladoMedio
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón actualizar
        Button(
            onClick = {
                val categoriaActualizada = Categoria(
                    idCategory = categoriaId,
                    nombreCategory = nombre,
                    descripcionCategory = descripcion,
                    imagenCategoria = imagenActual
                )
                viewModel.actualizarCategoria(categoriaActualizada, imageUri)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Text("Actualizar Categoría", color = Color.White)
            }
        }
    }
}