package com.app.myappdeinventario.repository

import android.net.Uri
import android.util.Log
import com.app.myappdeinventario.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductoRepo {

    private val db = FirebaseFirestore.getInstance()
    private val productoCollection = db.collection("producto")
    private val storageRef = FirebaseStorage.getInstance().reference

     //Agrega un nuevo producto a Firestore.
     //Retorna el ID generado o null si hay error.

    suspend fun addProducto(producto: Producto): String? {
        return try {
            val docRef = productoCollection.document() // Genera ID único
            val productoConId = producto.copy(idProduct = docRef.id)
            docRef.set(productoConId).await()
            Log.d("ProductoRepo", "Producto agregado con ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al guardar producto", e)
            null
        }
    }

     //Sube imágenes a Firebase Storage y devuelve las URLs descargables.

    suspend fun uploadImages(uris: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        for (uri in uris) {
            try {
                val fileName = "productos/${UUID.randomUUID()}.jpg"
                val fileRef = storageRef.child(fileName)
                fileRef.putFile(uri).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()
                urls.add(downloadUrl)
                Log.d("ProductoRepo", "Imagen subida: $downloadUrl")
            } catch (e: Exception) {
                Log.e("ProductoRepo", "Error subiendo imagen: ${uri.path}", e)
            }
        }
        return urls
    }


    //Obtiene la lista completa de productos desde Firestore.

    suspend fun getProductos(): List<Producto> {
        return try {
            val snapshot = productoCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Producto::class.java) }
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al obtener productos", e)
            emptyList()
        }
    }

     //Edita un producto existente.

    suspend fun editProducto(idProduct: String, producto: Producto): Boolean {
        return try {
            productoCollection.document(idProduct).set(producto).await()
            Log.d("ProductoRepo", "Producto actualizado: $idProduct")
            true
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al actualizar producto", e)
            false
        }
    }


    //Elimina un producto por ID.

    suspend fun deleteProducto(idProduct: String): Boolean {
        return try {
            productoCollection.document(idProduct).delete().await()
            Log.d("ProductoRepo", "Producto eliminado: $idProduct")
            true
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al eliminar producto", e)
            false
        }
    }
}
