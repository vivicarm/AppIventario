package com.app.myappdeinventario.views

import android.Manifest
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
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
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

class AgregarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                CrearCategoria()
            }
        }
    }
}

@Composable
fun CrearCategoria(viewModel: CategoriaViewModel = viewModel()) {
    val context = LocalContext.current
    val mensaje by viewModel.mensajeEstado.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var categoria by remember { mutableStateOf(Categoria()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 🔹 Launcher para abrir galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // 🔹 Launcher para solicitar permiso y abrir galería si se concede
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Mostrar mensaje de ViewModel
    LaunchedEffect(mensaje) {
        mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🟩 Encabezado
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
                Text(
                    text = "Crea una nueva Categoría",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🖼️ Imagen seleccionada
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
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(130.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen seleccionada", color = Color.Gray, modifier = Modifier.padding(30.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🔹 Botón seleccionar imagen
                IconButton(onClick = {
                    val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    // ✅ Si el permiso ya fue concedido → abrir galería directamente
                    if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        permisoLauncher.launch(permiso)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Seleccionar imagen",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // nombre categoría
        OutlinedTextField(
            value = categoria.nombreCategory,
            onValueChange = { categoria = categoria.copy(nombreCategory = it) },
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


        // descripción
        OutlinedTextField(
            value = categoria.descripcionCategory,
            onValueChange = { categoria = categoria.copy(descripcionCategory = it) },
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

        // botón guardar
        Button(
            onClick = {
                if (imageUri == null) {
                    Toast.makeText(context, "Selecciona una imagen antes de guardar", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.agregarCategoria(categoria, imageUri)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40))
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Text("Guardar Categoría", color = Color.White)
            }
        }
    }
}
