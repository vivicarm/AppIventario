package com.app.myappdeinventario.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Categoria
import com.app.myappdeinventario.repository.CategoryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CategoriaViewModel: ViewModel() {
    private val repo = CategoryRepo()
    
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    // Estado para mostrar mensajes o resultados
    private val _mensajeEstado = MutableStateFlow<String?>(null)
    val mensajeEstado: StateFlow<String?> = _mensajeEstado

    // Estado de conexión
    private val _estadoConexion = MutableStateFlow<String?>(null)
    val estadoConexion: StateFlow<String?> = _estadoConexion

    // Estado para mostrar progreso o loading
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    // Estado para skeleton loading
    private val _cargandoLista = MutableStateFlow(false)
    val cargandoLista: StateFlow<Boolean> = _cargandoLista

    init {
        cargarCategorias()
    }

    // Cargar todas las categorías
    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                _cargandoLista.value = true
                val resultado = repo.obtenerCategorias()

                resultado.fold(
                    onSuccess = { listaCategorias ->
                        _categorias.value = listaCategorias
                    },
                    onFailure = { error ->
                        _mensajeEstado.value = "❌ Error al cargar categorías: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargandoLista.value = false
            }
        }
    }

    // Agregar categoría (con actualización optimista)
    fun agregarCategoria(categoria: Categoria, imageUri: Uri?) {
        if (categoria.nombreCategory.isBlank()) {
            _mensajeEstado.value = "⚠️ El nombre de la categoría es obligatorio."
            return
        }

        if (imageUri == null) {
            _mensajeEstado.value = "⚠️ Debes seleccionar una imagen antes de guardar."
            return
        }

        // Crear categoría temporal con ID único para UI inmediata
        val tempId = UUID.randomUUID().toString()
        val categoriaTemporal = categoria.copy(idCategory = tempId, imagenCategoria = "loading") // Placeholder para imagen

        // Actualización optimista: agregar inmediatamente a la lista
        val currentList = _categorias.value.toMutableList()
        currentList.add(categoriaTemporal)
        _categorias.value = currentList
        _estadoConexion.value = "⏳ Creando categoría..."

        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.agregarCategoria(categoria.copy(idCategory = tempId), imageUri)

                resultado.fold(
                    onSuccess = { savedId ->
                        // El categoría ya tiene el ID correcto, solo actualizar la imagen
                        val updatedList = _categorias.value.toMutableList()
                        val index = updatedList.indexOfFirst { it.idCategory == tempId }
                        if (index != -1) {
                            updatedList[index] = categoria.copy(idCategory = tempId, imagenCategoria = savedId)
                            _categorias.value = updatedList
                        }
                        _estadoConexion.value = "✅ Categoría creada exitosamente"
                        _mensajeEstado.value = "✅ Categoría guardada correctamente."
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista en caso de error
                        val updatedList = _categorias.value.toMutableList()
                        updatedList.remove(categoriaTemporal)
                        _categorias.value = updatedList
                        _estadoConexion.value = "❌ Error al crear categoría"
                        _mensajeEstado.value = "❌ Error al guardar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista en caso de error
                val updatedList = _categorias.value.toMutableList()
                updatedList.remove(categoriaTemporal)
                _categorias.value = updatedList
                _estadoConexion.value = "❌ Error de conexión"
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualizar categoría (con actualización optimista)
    fun actualizarCategoria(categoria: Categoria, imageUri: Uri?) {
        if (categoria.nombreCategory.isBlank()) {
            _mensajeEstado.value = "⚠️ El nombre de la categoría es obligatorio."
            return
        }

        // Guardar el estado original para revertir en caso de error
        val originalList = _categorias.value.toList()

        // Actualización optimista: actualizar inmediatamente en la lista
        val currentList = _categorias.value.toMutableList()
        val index = currentList.indexOfFirst { it.idCategory == categoria.idCategory }
        if (index != -1) {
            // Si hay nueva imagen, usar placeholder temporal
            val categoriaOptimizada = if (imageUri != null) {
                categoria.copy(imagenCategoria = "loading")
            } else {
                categoria
            }
            currentList[index] = categoriaOptimizada
            _categorias.value = currentList
        }
        _estadoConexion.value = "⏳ Actualizando categoría..."

        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.actualizarCategoria(categoria, imageUri)

                resultado.fold(
                    onSuccess = {
                        // Si la actualización fue exitosa, recargar para obtener datos frescos
                        // Solo recargar si no hay categorías temporales en la lista
                        if (_categorias.value.none { it.idCategory.startsWith("temp_") }) {
                            cargarCategorias()
                        } else {
                            // Si hay categorías temporales, solo actualizar la categoría actualizada
                            val currentList = _categorias.value.toMutableList()
                            val index = currentList.indexOfFirst { it.idCategory == categoria.idCategory }
                            if (index != -1) {
                                currentList[index] = categoria.copy(imagenCategoria = resultado.getOrNull() ?: categoria.imagenCategoria)
                                _categorias.value = currentList
                            }
                        }
                        _estadoConexion.value = "✅ Categoría actualizada exitosamente"
                        _mensajeEstado.value = "✅ Categoría actualizada correctamente."
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista en caso de error
                        _categorias.value = originalList
                        _estadoConexion.value = "❌ Error al actualizar categoría"
                        _mensajeEstado.value = "❌ Error al actualizar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista en caso de error
                _categorias.value = originalList
                _estadoConexion.value = "❌ Error de conexión"
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Eliminar categoría (con actualización optimista)
    fun eliminarCategoria(categoria: Categoria) {
        // Guardar el estado original para revertir en caso de error
        val originalList = _categorias.value.toList()

        // Actualización optimista: remover inmediatamente de la lista
        val currentList = _categorias.value.toMutableList()
        currentList.remove(categoria)
        _categorias.value = currentList
        _estadoConexion.value = "⏳ Eliminando categoría..."

        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.eliminarCategoria(categoria.idCategory, categoria.imagenCategoria)

                resultado.fold(
                    onSuccess = {
                        _estadoConexion.value = "✅ Categoría eliminada exitosamente"
                        _mensajeEstado.value = "✅ Categoría eliminada correctamente."
                        // No recargar lista ya que la eliminación optimista ya la removió
                    },
                    onFailure = { error ->
                        // Revertir actualización optimista: restaurar la lista original
                        _categorias.value = originalList
                        _estadoConexion.value = "❌ Error al eliminar categoría"
                        _mensajeEstado.value = "❌ Error al eliminar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                // Revertir actualización optimista: restaurar la lista original
                _categorias.value = originalList
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
