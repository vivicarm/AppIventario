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

            val docRef = categoriaCollection.document()
            val categoriaConId = categoria.copy(
                idCategory = docRef.id,
                imagenCategoria = imageUrl ?: ""
            )

            docRef.set(categoriaConId).await()
            Log.d("CategoryRepo", "Categoría agregada con ID: ${docRef.id}")

            Result.success(docRef.id)
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

}