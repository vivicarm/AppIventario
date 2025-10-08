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
    private val storage = FirebaseStorage.getInstance().reference
    suspend fun addProducto(producto: Producto): String?{
        return try {
            val docRef = productoCollection.document() // genera un ID único
            val productoConId = producto.copy(idProduct = docRef.id)
            docRef.set(productoConId).await()
            docRef.id
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error al guardar producto", e)
            null
        }
    }
    suspend fun uploadImages(uris: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        try {
            for (uri in uris) {
                val fileRef = storage.child("productos/${UUID.randomUUID()}.jpg")
                fileRef.putFile(uri).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()
                urls.add(downloadUrl)
            }
        } catch (e: Exception) {
            Log.e("ProductoRepo", "Error subiendo imágenes", e)
        }
        return urls
    }
    suspend fun getProducto(): List<Producto>{
        val querySnapshot = productoCollection.get().await()
        return querySnapshot.documents.mapNotNull {  it.toObject(Producto::class.java)}
    }


    suspend fun editProducto(idProduct: String, producto: Producto) {
        productoCollection.document(idProduct).set(producto).await()
    }


    suspend fun deleteProducto(idProduct: String) {
        productoCollection.document(idProduct).delete().await()
    }
}