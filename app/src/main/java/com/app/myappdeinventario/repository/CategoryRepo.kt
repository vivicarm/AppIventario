package com.app.myappdeinventario.repository

import android.net.Uri
import android.util.Log
import com.app.myappdeinventario.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class CategoryRepo {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val categoriaCollection = db.collection("categorias")

    // Agregar categoría
    suspend fun agregarCategoria(categoria: Categoria, imageUri: Uri?): Result<String> {
        return try {
            var imageUrl: String? = null

            if (imageUri != null) {
                val imageName = "categorias/${System.currentTimeMillis()}.jpg"
                val storageRef = storage.reference.child(imageName)

                Log.d("CategoryRepo", "Subiendo imagen: $imageName")
                storageRef.putFile(imageUri).await()

                imageUrl = storageRef.downloadUrl.await().toString()
                Log.d("CategoryRepo", "URL de imagen: $imageUrl")
            }

            // Usar el ID que ya viene del cliente (UUID generado)
            val categoriaId = categoria.idCategory
            val docRef = categoriaCollection.document(categoriaId)
            val categoriaConImagen = categoria.copy(imagenCategoria = imageUrl ?: "")

            docRef.set(categoriaConImagen).await()
            Log.d("CategoryRepo", "Categoría agregada con ID: $categoriaId")

            Result.success(imageUrl ?: "")
        } catch (e: Exception) {
            Log.e("CategoryRepo", "Error al guardar categoría", e)
            val errorMsg = when {
                e.message?.contains("Object does not exist") == true ->
                    "La imagen no existe o no se pudo subir correctamente."
                e.message?.contains("permission-denied") == true ->
                    "No tienes permisos para guardar esta categoría."
                else -> "Ocurrió un error al guardar la categoría."
            }
            Result.failure(Exception(errorMsg))
        }
    }

    // Listar todas las categorías
    suspend fun obtenerCategorias(): Result<List<Categoria>> {
        return try {
            Log.d("CategoryRepo", "Obteniendo categorías...")
            val snapshot = categoriaCollection.get().await()
            val categorias = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Categoria::class.java)
            }
            Log.d("CategoryRepo", "Categorías obtenidas: ${categorias.size}")
            Result.success(categorias)
        } catch (e: Exception) {
            Log.e("CategoryRepo", "Error al obtener categorías", e)
            Result.failure(e)
        }
    }

    // Actualizar categoría
    suspend fun actualizarCategoria(categoria: Categoria, imageUri: Uri?): Result<String> {
        return try {
            var imageUrl = categoria.imagenCategoria

            // Si hay una nueva imagen, subirla
            if (imageUri != null) {
                // Eliminar imagen anterior si existe
                if (!categoria.imagenCategoria.isNullOrEmpty()) {
                    try {
                        val oldImageRef = storage.getReferenceFromUrl(categoria.imagenCategoria!!)
                        oldImageRef.delete().await()
                        Log.d("CategoryRepo", "Imagen anterior eliminada")
                    } catch (e: Exception) {
                        Log.w("CategoryRepo", "No se pudo eliminar imagen anterior: ${e.message}")
                    }
                }

                // Subir nueva imagen
                val imageName = "categorias/${System.currentTimeMillis()}.jpg"
                val storageRef = storage.reference.child(imageName)
                storageRef.putFile(imageUri).await()
                imageUrl = storageRef.downloadUrl.await().toString()
                Log.d("CategoryRepo", "Nueva imagen subida: $imageUrl")
            }

            val categoriaActualizada = categoria.copy(imagenCategoria = imageUrl)
            categoriaCollection.document(categoria.idCategory).set(categoriaActualizada).await()
            Log.d("CategoryRepo", "Categoría actualizada: ${categoria.idCategory}")

            Result.success(categoria.idCategory)
        } catch (e: Exception) {
            Log.e("CategoryRepo", "Error al actualizar categoría", e)
            Result.failure(e)
        }
    }

    // Eliminar categoría
    suspend fun eliminarCategoria(categoriaId: String, imageUrl: String?): Result<Unit> {
        return try {
            // Eliminar imagen de Storage si existe
            if (!imageUrl.isNullOrEmpty()) {
                try {
                    val imageRef = storage.getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                    Log.d("CategoryRepo", "Imagen eliminada de Storage")
                } catch (e: Exception) {
                    Log.w("CategoryRepo", "No se pudo eliminar imagen: ${e.message}")
                }
            }

            // Eliminar documento de Firestore
            categoriaCollection.document(categoriaId).delete().await()
            Log.d("CategoryRepo", "Categoría eliminada: $categoriaId")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CategoryRepo", "Error al eliminar categoría", e)
            Result.failure(e)
        }
    }
}