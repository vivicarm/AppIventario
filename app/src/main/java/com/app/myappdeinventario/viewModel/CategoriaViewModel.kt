package com.app.myappdeinventario.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.Categoria
import com.app.myappdeinventario.model.Producto
import com.app.myappdeinventario.repository.CategoryRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaViewModel: ViewModel() {
    private val repo = CategoryRepo()
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    // Estado para mostrar mensajes o resultados
    private val _mensajeEstado = MutableStateFlow<String?>(null)
    val mensajeEstado: StateFlow<String?> = _mensajeEstado

    // Estado para mostrar progreso o loading
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun agregarCategoria(categoria: Categoria, imageUri: Uri?) {
        if (categoria.nombreCategory.isBlank()) {
            _mensajeEstado.value = "⚠️ El nombre de la categoría es obligatorio."
            return
        }

        if (imageUri == null) {
            _mensajeEstado.value = "⚠️ Debes seleccionar una imagen antes de guardar."
            return
        }


        viewModelScope.launch {
            try {
                _cargando.value = true
                val resultado = repo.agregarCategoria(categoria, imageUri)

                resultado.fold(
                    onSuccess = {
                        _mensajeEstado.value = "✅ Categoría guardada correctamente."
                    },
                    onFailure = { error ->
                        _mensajeEstado.value = "❌ Error al guardar: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                _mensajeEstado.value = "❌ Error inesperado: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensajeEstado.value = null
    }
}
