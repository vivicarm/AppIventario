package com.app.myappdeinventario.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.repository.ProductoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepo()

    // Estado de productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado de carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Mensajes de estado (éxito o error)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje


    fun CrearProducto(producto: Producto, uris: List<Uri>) {
        viewModelScope.launch {
            // 🔹 Validaciones previas
            if (producto.nombre.isBlank()) {
                _mensaje.value = "El nombre no puede estar vacío."
                return@launch
            }
            if (producto.precioC <= 0.0 || producto.precioV <= 0.0) {
                _mensaje.value = "Los precios deben ser mayores a 0."
                return@launch
            }

            _loading.value = true
            _mensaje.value = null

            try {
                // 1️⃣ Subir imágenes si existen
                val urls = if (uris.isNotEmpty()) {
                    repository.uploadImages(uris)
                } else emptyList()

                // 2️⃣ Crear copia del producto con las URLs subidas
                val productoFinal = producto.copy(image = urls)

                // 3️⃣ Guardar producto en Firestore
                repository.addProducto(productoFinal)

                // 4️⃣ Recargar la lista actualizada
                _productos.value = repository.getProductos()

                // 5️⃣ Mensaje de éxito
                _mensaje.value = "✅ Producto guardado correctamente con sus imágenes."
            } catch (e: Exception) {
                // 6️⃣ En caso de error
                _mensaje.value = "❌ Error al guardar el producto: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadProducto() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _productos.value = repository.getProductos()
                _mensaje.value = "Productos cargados correctamente ✅"
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar productos: ${e.message}"
            } finally {
                _loading.value = false
            }
        }

    }

    fun deleteProducto(idProduct: String) {
        viewModelScope.launch {
            if (idProduct.isBlank()) {
                _mensaje.value = "ID de producto inválido."
                return@launch
            }

            try {
                repository.deleteProducto(idProduct)
                loadProducto()
                _mensaje.value = "Producto eliminado correctamente 🗑️"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar producto: ${e.message}"
            }
        }
    }

    fun editProducto(idProduct: String, producto: Producto) {
        viewModelScope.launch {
            if (idProduct.isBlank()) {
                _mensaje.value = "ID de producto inválido."
                return@launch
            }
            if (producto.nombre.isBlank()) {
                _mensaje.value = "El nombre no puede estar vacío."
                return@launch
            }

            try {
                repository.editProducto(idProduct, producto)
                loadProducto()
                _mensaje.value = "Producto actualizado correctamente "
            } catch (e: Exception) {
                _mensaje.value = "Error al editar producto: ${e.message}"
            }
        }
    }
}
