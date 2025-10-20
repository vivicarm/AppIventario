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
    private val storage = FirebaseStorage.getInstance()
    private val productoCollection = db.collection("productos")

    // Agregar producto
    suspend fun agregarProducto(producto: Producto): Result<String> {
        return try {
            // Usar el ID que ya viene del cliente (UUID generado)
            val productoId = producto.idProduct
            val docRef = productoCollection.document(productoId)
            docRef.set(producto).await()
            Log.d("ProductoRepo", "Producto agregado con ID: $productoId")
            Result.success(productoId)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al guardar producto", e)
            Result.failure(e)
        }
    }

    // Subir imágenes a Storage
    suspend fun subirImagenes(imageUris: List<Uri>): Result<List<String>> {
        return try {
            val imageUrls = mutableListOf<String>()
            for (uri in imageUris) {
                val imageName = "productos/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(imageName)
                
                Log.d("ProductoRepo", "Subiendo imagen: $imageName")
                storageRef.putFile(uri).await()
                
                val downloadUrl = storageRef.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
                Log.d("ProductoRepo", "URL de imagen: $downloadUrl")
            }
            Result.success(imageUrls)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al subir imágenes", e)
            Result.failure(e)
        }
    }

    // Listar todos los productos
    suspend fun obtenerProductos(): Result<List<Producto>> {
        return try {
            Log.d("ProductoRepo", "Obteniendo productos...")
            val snapshot = productoCollection.get().await()
            val productos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Producto::class.java)
            }
            Log.d("ProductoRepo", "Productos obtenidos: ${productos.size}")
            Result.success(productos)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al obtener productos", e)
            Result.failure(e)
        }
    }

    // Actualizar producto
    suspend fun actualizarProducto(producto: Producto, nuevasImageUris: List<Uri>?): Result<String> {
        return try {
            var imageUrls = producto.image

            // Si hay nuevas imágenes, subirlas
            if (!nuevasImageUris.isNullOrEmpty()) {
                // Eliminar imágenes anteriores
                for (oldImageUrl in producto.image) {
                    try {
                        val oldImageRef = storage.getReferenceFromUrl(oldImageUrl)
                        oldImageRef.delete().await()
                        Log.d("ProductoRepo", "Imagen anterior eliminada")
                    } catch (e: Exception) {
                        Log.w("ProductoRepo", "No se pudo eliminar imagen anterior: ${e.message}")
                    }
                }

                // Subir nuevas imágenes
                val resultado = subirImagenes(nuevasImageUris)
                if (resultado.isSuccess) {
                    imageUrls = resultado.getOrNull() ?: emptyList()
                }
            }

            val productoActualizado = producto.copy(image = imageUrls)
            productoCollection.document(producto.idProduct).set(productoActualizado).await()
            Log.d("ProductoRepo", "Producto actualizado: ${producto.idProduct}")

            Result.success(producto.idProduct)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al actualizar producto", e)
            Result.failure(e)
        }
    }

    // Eliminar producto
    suspend fun eliminarProducto(productoId: String, imageUrls: List<String>): Result<Unit> {
        return try {
            // Eliminar imágenes de Storage
            for (imageUrl in imageUrls) {
                try {
                    val imageRef = storage.getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                    Log.d("ProductoRepo", "Imagen eliminada de Storage")
                } catch (e: Exception) {
                    Log.w("ProductoRepo", "No se pudo eliminar imagen: ${e.message}")
                }
            }

            // Eliminar documento de Firestore
            productoCollection.document(productoId).delete().await()
            Log.d("ProductoRepo", "Producto eliminado: $productoId")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al eliminar producto", e)
            Result.failure(e)
        }
    }
}
