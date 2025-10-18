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
    private val productoCollection = db.collection("productos")


    suspend fun agregarProducto(producto: Producto): Result<String> {
        return try {
            val docRef = productoCollection.document()
            val productoConId = producto.copy(idProduct = docRef.id)
            docRef.set(productoConId).await()
            Log.d("ProductoRepo", "Producto agregado con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al guardar producto", e)
            Result.failure(e)
        }
    }
}
