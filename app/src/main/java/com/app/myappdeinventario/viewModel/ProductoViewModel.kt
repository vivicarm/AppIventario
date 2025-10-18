package com.app.myappdeinventario.viewModel

import android.net.Uri
import android.widget.Toast
import android.content.Context
import java.util.UUID
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.repository.ProductoRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductoViewModel : ViewModel() {

    private val repo = ProductoRepo()
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado de carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Mensajes de estado (éxito o error)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repo.agregarProducto(producto)
                if (result.isSuccess) {
                    _mensaje.value = "✅ Producto agregado (ID: ${result.getOrNull()})"
                } else {
                    _mensaje.value = "❌ Error: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _mensaje.value = "❌ Excepción: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun agregarProductoConImagenes(context: Context, producto: Producto, imageUris: List<Uri>) {
        viewModelScope.launch {
            try {
                // Subir imágenes a Firebase Storage y obtener URLs
                val imageUrls = mutableListOf<String>()
                for (uri in imageUris) {
                    val ref = FirebaseStorage.getInstance()
                        .reference
                        .child("productos/${UUID.randomUUID()}.jpg")

                    ref.putFile(uri).await()
                    val downloadUrl = ref.downloadUrl.await().toString()
                    imageUrls.add(downloadUrl)
                }

                // Crear copia del producto con URLs
                val productoConImagenes = producto.copy(image = imageUrls)

                // Guardar en Firestore
                FirebaseFirestore.getInstance()
                    .collection("productos")
                    .add(productoConImagenes)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


}
