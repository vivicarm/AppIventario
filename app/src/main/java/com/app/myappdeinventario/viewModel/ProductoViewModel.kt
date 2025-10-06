package com.app.myappdeinventario.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.repository.ProductoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel: ViewModel() {

    private val repository = ProductoRepo()

    // Estado de los productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado de carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProducto() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _productos.value = repository.getProducto()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }

    }

    fun saveProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.addProducto(producto)
                loadProducto() // refrescar lista después de guardar
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun editProducyo(idProduct: String, producto: Producto) {
        viewModelScope.launch {
            try {
                repository.editProducto(idProduct, producto)
                loadProducto()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }

    }

    fun deleteProducto(idProduct: String) {
        viewModelScope.launch {
            try {
                repository.deleteProducto(idProduct)
                loadProducto()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun saveProductoConImagenes(producto: Producto, uris: List<Uri>) {
        viewModelScope.launch {
            try {
                val urls = if (uris.isNotEmpty()) repository.uploadImages(uris) else emptyList()
                val productoFinal = producto.copy(image = urls)

                repository.addProducto(productoFinal)
                loadProducto()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}