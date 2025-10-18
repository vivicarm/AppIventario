package com.app.myappdeinventario.views

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.app.myappdeinventario.R
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.viewModel.ProductoViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdeOscuroProfundo
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

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

@Composable
fun CrearProducto(viewModel: ProductoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    var producto by remember { mutableStateOf(Producto()) } // llama al model categoria
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Variables auxiliares de texto para los doubles
    var precioc by remember { mutableStateOf(producto.precioC.toString()) }
    var preciov by remember { mutableStateOf(producto.precioV.toString()) }
    var precioP by remember { mutableStateOf(producto.precioPromocion.toString()) }

    // Lista de imágenes seleccionadas
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    // Tomar foto con cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convertimos el Bitmap en Uri temporal para mostrarlo
            val uri = saveBitmapToCache(context, it)
            imageUris = imageUris + uri
        }
    }

    // Seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        // Agregamos las imágenes seleccionadas a la lista existente
        if (uris != null) {
            imageUris = imageUris + uris
        }
    }

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
                    text = "Crea un nuevo producto",
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

        Spacer(modifier = Modifier.height(15.dp))

        // Card para imágenes
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
                if (imageUris.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(imageUris.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUris[index]),
                                contentDescription = "Imagen del producto",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No hay imágenes seleccionadas",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconButton(onClick = {
                        galleryLauncher.launch("image/*")
                    }) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Seleccionar desde galería",
                            tint = verdeAzuladoMedio
                        )
                    }

                    IconButton(onClick = {
                        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        cameraLauncher.launch(null)
                    }) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            tint = verdeAzuladoMedio
                        )
                    }
                }
            }
        }

        // Campo nombre
        OutlinedTextField(
            value = producto.nombre,
            onValueChange = { producto = producto.copy(nombre = it) },
            label = { Text("Nombre") },
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

        // Campo descripción
        OutlinedTextField(
            value = producto.descripcion,
            onValueChange = { producto = producto.copy(descripcion = it)},
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

        //Campo stock
        OutlinedTextField(
            value = producto.stock.toString(),
            onValueChange = {newValue -> val newStock = newValue.toIntOrNull() ?: 0 // Evita error si el campo queda vacío
                producto = producto.copy(stock = newStock)},
            label = { Text("stock del producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = verdePetróleoOscuro,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = verdeAzuladoMedio,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = verdeAzuladoMedio,
                unfocusedLabelColor = Color.Gray,
                cursorColor = verdeAzuladoMedio
            )
        )

        // Campo precio compra
        OutlinedTextField(
            value = precioc,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    precioc = newValue
                    producto = producto.copy(precioC = newValue.toDoubleOrNull() ?: 0.0)
                }
            },
            label = { Text("Precio de compra ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

        // Campo precio venta
        OutlinedTextField(
            value = preciov,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    preciov = newValue
                    producto = producto.copy(precioV = newValue.toDoubleOrNull() ?: 0.0)
                }
            },
            label = { Text("Precio de venta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

        // Campo precio promoción
        OutlinedTextField(
            value = precioP,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    precioP = newValue
                    producto = producto.copy(precioPromocion = newValue.toDoubleOrNull() ?: 0.0)
                }
            },
            label = { Text("Precio promoción") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

        // Campo fecha
        OutlinedTextField(
            value = producto.fechaCreacion,
            onValueChange = { producto = producto.copy(fechaCreacion = it) },
            label = { Text("Fecha de ingreso") },
            modifier = Modifier.fillMaxWidth(),colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = verdePetróleoOscuro,
                unfocusedTextColor = Color.Black),
            trailingIcon = {
                IconButton(onClick = {
                    val calendario = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            producto = producto.copy(fechaCreacion = "$d/${m + 1}/$y")
                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                    ).show() }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // botón guardar producto

        Button( onClick = {
            viewModel.agregarProductoConImagenes(context, producto, imageUris) },
            modifier = Modifier .fillMaxWidth() .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeOscuroProfundo) )
        { Text("Guardar producto", color = Color.White, fontSize = 18.sp) }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}




