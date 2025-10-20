package com.app.myappdeinventario.views

import android.Manifest
import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.app.myappdeinventario.R
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.viewModel.ProductoViewModel
import com.app.myappdeinventario.views.ui.theme.MyAppDeInventarioTheme
import com.app.myappdeinventario.views.ui.theme.verdeAzuladoMedio
import com.app.myappdeinventario.views.ui.theme.verdePetróleoOscuro
import java.util.Calendar

class EditarProductoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val productoId = intent.getStringExtra("productoId") ?: ""
        val productoNombre = intent.getStringExtra("productoNombre") ?: ""
        val productoDescripcion = intent.getStringExtra("productoDescripcion") ?: ""
        val productoPrecioC = intent.getDoubleExtra("productoPrecioC", 0.0)
        val productoPrecioV = intent.getDoubleExtra("productoPrecioV", 0.0)
        val productoPrecioPromocion = intent.getDoubleExtra("productoPrecioPromocion", 0.0)
        val productoStock = intent.getIntExtra("productoStock", 0)
        val productoFecha = intent.getStringExtra("productoFecha") ?: ""
        val productoImagenes = intent.getStringArrayExtra("productoImagenes")?.toList() ?: emptyList()
        val productoCategoria = intent.getStringExtra("productoCategoria") ?: ""

        setContent {
            MyAppDeInventarioTheme {
                EditarProductoScreen(
                    productoId = productoId,
                    productoNombre = productoNombre,
                    productoDescripcion = productoDescripcion,
                    productoPrecioC = productoPrecioC,
                    productoPrecioV = productoPrecioV,
                    productoPrecioPromocion = productoPrecioPromocion,
                    productoStock = productoStock,
                    productoFecha = productoFecha,
                    productoImagenes = productoImagenes,
                    productoCategoria = productoCategoria
                )
            }
        }
    }
}

@Composable
fun EditarProductoScreen(
    productoId: String,
    productoNombre: String,
    productoDescripcion: String,
    productoPrecioC: Double,
    productoPrecioV: Double,
    productoPrecioPromocion: Double,
    productoStock: Int,
    productoFecha: String,
    productoImagenes: List<String>,
    productoCategoria: String,
    viewModel: ProductoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val mensaje by viewModel.mensajeEstado.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var nombre by remember { mutableStateOf(productoNombre) }
    var descripcion by remember { mutableStateOf(productoDescripcion) }
    var precioC by remember { mutableStateOf(productoPrecioC.toString()) }
    var precioV by remember { mutableStateOf(productoPrecioV.toString()) }
    var precioPromocion by remember { mutableStateOf(productoPrecioPromocion.toString()) }
    var stock by remember { mutableStateOf(productoStock.toString()) }
    var fecha by remember { mutableStateOf(productoFecha) }
    var imagenesActuales by remember { mutableStateOf(productoImagenes) }
    var nuevasImageUris by remember { mutableStateOf<List<Uri>?>(null) }

    // Launcher para abrir galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            nuevasImageUris = uris
        }
    }

    // Launcher para solicitar permiso
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch("image/*")
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
            if (it.contains("actualizado correctamente")) {
                val intent = Intent(context, ListarProductoActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            val intent = Intent(context, ListarProductoActivity::class.java)
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
                        text = "Editar Producto",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

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
                if (nuevasImageUris != null) {
                    // Mostrar nuevas imágenes seleccionadas
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(nuevasImageUris!!.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(nuevasImageUris!![index]),
                                contentDescription = "Nueva imagen",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else if (imagenesActuales.isNotEmpty()) {
                    // Mostrar imágenes actuales
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(imagenesActuales.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(imagenesActuales[index]),
                                contentDescription = "Imagen actual",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text("Sin imágenes", color = Color.Gray, modifier = Modifier.padding(30.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón seleccionar imágenes
                IconButton(onClick = {
                    val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permisoLauncher.launch(permiso)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Cambiar imágenes",
                        tint = verdeAzuladoMedio,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Campo nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
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

        // Campo stock
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock del producto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

        // Campo precio compra
        OutlinedTextField(
            value = precioC,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    precioC = newValue
                }
            },
            label = { Text("Precio de compra") },
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
            value = precioV,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    precioV = newValue
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
            value = precioPromocion,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    precioPromocion = newValue
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
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha de ingreso") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = verdePetróleoOscuro,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = verdeAzuladoMedio,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = verdeAzuladoMedio,
                unfocusedLabelColor = Color.Gray,
                cursorColor = verdeAzuladoMedio
            ),
            trailingIcon = {
                IconButton(onClick = {
                    val calendario = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            fecha = "$d/${m + 1}/$y"
                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Botón actualizar
        Button(
            onClick = {
                val productoActualizado = Producto(
                    idProduct = productoId,
                    nombre = nombre,
                    descripcion = descripcion,
                    precioC = precioC.toDoubleOrNull() ?: 0.0,
                    precioV = precioV.toDoubleOrNull() ?: 0.0,
                    precioPromocion = precioPromocion.toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    fechaCreacion = fecha,
                    image = imagenesActuales,
                    idCategory = productoCategoria
                )
                viewModel.actualizarProducto(productoActualizado, nuevasImageUris)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
            enabled = !cargando
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Text("Actualizar Producto", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}