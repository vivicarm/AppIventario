package com.app.myappdeinventario.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.repository.ProductoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProductoViewModel : ViewModel() {

    private val repo = ProductoRepo()
    
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado de carga para operaciones
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    // Estado de carga para lista (skeleton)
    private val _cargandoLista = MutableStateFlow(false)
    val cargandoLista: StateFlow<Boolean> = _cargandoLista

    // Mensajes de estado
    private val _mensajeEstado = MutableStateFlow<String?>(null)
    val mensajeEstado: StateFlow<String?> = _mensajeEstado

    // Estado de conexión
    private val _estadoConexion = MutableStateFlow<String?>(null)
    val estadoConexion: StateFlow<String?> = _estadoConexion

    init {
        cargarProductos()
    }

    // Cargar todos los productos
    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _cargandoLista.value = true
                val resultado = repo.obtenerProductos()

                resultado.fold(
                    onSuccess = { listaProductos ->
                        _productos.value = listaProductos
                    },
                    onFailure = { error ->
                        _mensajeEstado.value = "❌ Error al cargar productos: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargandoLista.value = false
            }
        }
    }

    // Agregar producto con imágenes (con actualización optimista)
    fun agregarProductoConImagenes(producto: Producto, imageUris: List<Uri>) {
        if (producto.nombre.isBlank()) {
            _mensajeEstado.value = "⚠️ El nombre del producto es obligatorio."
            return
        }

        if (imageUris.isEmpty()) {
            _mensajeEstado.value = "⚠️ Debes seleccionar al menos una imagen."
            return
        }

        // Crear producto temporal con ID único para UI inmediata
        val tempId = UUID.randomUUID().toString()
        val productoTemporal = producto.copy(idProduct = tempId, image = imageUris.map { "loading" }) // Placeholder para imágenes

        // Actualización optimista: agregar inmediatamente a la lista
        val currentList = _productos.value.toMutableList()
        currentList.add(productoTemporal)
        _productos.value = currentList
        _estadoConexion.value = "⏳ Creando producto..."

        viewModelScope.launch {
            try {
                _cargando.value = true

                // Subir imágenes
                val resultadoImagenes = repo.subirImagenes(imageUris)
                if (resultadoImagenes.isFailure) {
                    // Revertir actualización optimista en caso de error
                    val updatedList = _productos.value.toMutableList()
                    updatedList.remove(productoTemporal)
                    _productos.value = updatedList
                    _estadoConexion.value = "❌ Error al subir imágenes"
                    _mensajeEstado.value = "❌ Error al subir imágenes: ${resultadoImagenes.exceptionOrNull()?.message}"
                    return@launch
                }

                val imageUrls = resultadoImagenes.getOrNull() ?: emptyList()
                val productoConImagenes = producto.copy(image = imageUrls, idProduct = tempId)

                // Guardar producto (usa el ID UUID generado)
                val resultado = repo.agregarProducto(productoConImagenes)

                resultado.fold(
                    onSuccess = { savedId ->
                        // El producto ya tiene el ID correcto, solo actualizar las imágenes
                        val updatedList = _productos.value.toMutableList()
                        val index = updatedList.indexOfFirst { it.idProduct == tempId }
                        if (index != -1) {
                            updatedList[index] = productoConImagenes
                            _productos.value = updatedList
                        }
                        _estadoConexion.value = "✅ Producto creado exitosamente"
                        _mensajeEstado.value = "✅ Producto guardado correctamente."
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista en caso de error
                        val updatedList = _productos.value.toMutableList()
                        updatedList.remove(productoTemporal)
                        _productos.value = updatedList
                        _estadoConexion.value = "❌ Error al crear producto"
                        _mensajeEstado.value = "❌ Error al guardar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista en caso de error
                val updatedList = _productos.value.toMutableList()
                updatedList.remove(productoTemporal)
                _productos.value = updatedList
                _estadoConexion.value = "❌ Error de conexión"
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualizar producto (con actualización optimista)
    fun actualizarProducto(producto: Producto, nuevasImageUris: List<Uri>?) {
        if (producto.nombre.isBlank()) {
            _mensajeEstado.value = "⚠️ El nombre del producto es obligatorio."
            return
        }

        // Guardar el estado original para revertir en caso de error
        val originalList = _productos.value.toList()

        // Actualización optimista: actualizar inmediatamente en la lista
        val currentList = _productos.value.toMutableList()
        val index = currentList.indexOfFirst { it.idProduct == producto.idProduct }
        if (index != -1) {
            // Si hay nuevas imágenes, usar placeholder temporal
            val productoOptimizado = if (!nuevasImageUris.isNullOrEmpty()) {
                producto.copy(image = nuevasImageUris.map { "loading" })
            } else {
                producto
            }
            currentList[index] = productoOptimizado
            _productos.value = currentList
        }
        _estadoConexion.value = "⏳ Actualizando producto..."

        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.actualizarProducto(producto, nuevasImageUris)

                resultado.fold(
                    onSuccess = {
                        // Si la actualización fue exitosa, recargar para obtener datos frescos
                        // Solo recargar si no hay productos temporales en la lista
                        if (_productos.value.none { it.idProduct.startsWith("temp_") }) {
                            cargarProductos()
                        } else {
                            // Si hay productos temporales, solo actualizar el producto actualizado
                            val currentList = _productos.value.toMutableList()
                            val index = currentList.indexOfFirst { it.idProduct == producto.idProduct }
                            if (index != -1) {
                                currentList[index] = producto.copy(image = (resultado.getOrNull() as? List<String>) ?: producto.image)
                                _productos.value = currentList
                            }
                        }
                        _estadoConexion.value = "✅ Producto actualizado exitosamente"
                        _mensajeEstado.value = "✅ Producto actualizado correctamente."
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista en caso de error
                        _productos.value = originalList
                        _estadoConexion.value = "❌ Error al actualizar producto"
                        _mensajeEstado.value = "❌ Error al actualizar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista en caso de error
                _productos.value = originalList
                _estadoConexion.value = "❌ Error de conexión"
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Eliminar producto (con actualización optimista)
    fun eliminarProducto(producto: Producto) {
        // Guardar el estado original para revertir en caso de error
        val originalList = _productos.value.toList()

        // Actualización optimista: remover inmediatamente de la lista
        val currentList = _productos.value.toMutableList()
        currentList.remove(producto)
        _productos.value = currentList
        _estadoConexion.value = "⏳ Eliminando producto..."

        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.eliminarProducto(producto.idProduct, producto.image)

                resultado.fold(
                    onSuccess = {
                        _estadoConexion.value = "✅ Producto eliminado exitosamente"
                        _mensajeEstado.value = "✅ Producto eliminado correctamente."
                        // No recargar lista ya que la eliminación optimista ya la removió
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista: restaurar la lista original
                        _productos.value = originalList
                        _estadoConexion.value = "❌ Error al eliminar producto"
                        _mensajeEstado.value = "❌ Error al eliminar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista: restaurar la lista original
                _productos.value = originalList
                _estadoConexion.value = "❌ Error de conexión"
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensajeEstado.value = null
    }

    fun limpiarEstadoConexion() {
        _estadoConexion.value = null
    }
}
