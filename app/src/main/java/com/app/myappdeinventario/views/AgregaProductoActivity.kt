package com.app.myappdeinventario.views

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.viewModel.ProductoViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import java.io.ByteArrayOutputStream

class AgregaProductoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppDeInventarioTheme {
                CrearProducto()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProducto() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Productos", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { /* regresar */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Tabs
            ProductTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            // Formulario
            AddProductForm()
        }
    }
}

@Composable
fun ProductTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Stock", "Especificaciones", "Variantes")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, text ->
            TabItem(
                text = text,
                isSelected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFF6B4226) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
        }
        if (isSelected) {
            Divider(
                modifier = Modifier.width(60.dp),
                thickness = 3.dp,
                color = Color(0xFF6B4226)
            )
        }
    }
}

fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        bitmap,
        "IMG_${System.currentTimeMillis()}",
        null
    )
    return Uri.parse(path)
}

@Composable
fun AddProductForm(productoViewModel: ProductoViewModel = viewModel()) {

    // listas para guardar mas de ua imagen
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    // Permisos
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    val context = LocalContext.current

    // Cámara
    val tomarFotoDeLaCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imagen ->
            imagen?.let {
                imageBitmaps = imageBitmaps + it
            }
        }
    )

    // Galería
    val tomarImagenDeGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUris = imageUris + it
            }
        }
    )

    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var precioC by remember { mutableStateOf("") }
    var precioV by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Vista previa múltiple
        if (imageUris.isEmpty() && imageBitmaps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Vista previa de imágenes", color = Color.Gray)
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(imageBitmaps.size) { index ->
                    Image(
                        bitmap = imageBitmaps[index].asImageBitmap(),
                        contentDescription = "Imagen de cámara",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    )
                }
                items(imageUris.size) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUris[index]),
                        contentDescription = "Imagen de galería",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        // Botones de imagen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { tomarImagenDeGaleria.launch("image/*") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B4226))
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galería")
            }

            OutlinedButton(
                onClick = {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    tomarFotoDeLaCamara.launch(null)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B4226))
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cámara")
            }
        }

        // Campos de texto
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        OutlinedTextField(
            value = precioC,
            onValueChange = { precioC = it },
            label = { Text("Precio de Compra") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = precioV,
            onValueChange = { precioV = it },
            label = { Text("Precio de Venta") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val producto = Producto(
                    idProduct = "",
                    nombre = productName,
                    descripcion = description,
                    precioC = precioC.toDoubleOrNull() ?: 0.0,
                    precioV = precioV.toDoubleOrNull() ?: 0.0,
                    stock = 0,
                    image= emptyList() // se llenará en el ViewModel
                )

                // Convertir Bitmaps a URIs temporales
                val urisFromBitmaps = imageBitmaps.map { bitmap ->
                    bitmapToUri(context, bitmap)
                }

                val allUris = imageUris + urisFromBitmaps

                if (allUris.isNotEmpty()) {
                    productoViewModel.saveProductoConImagenes(producto, allUris)
                } else {
                    productoViewModel.saveProducto(producto)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4226)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Agregar Producto", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }

}
