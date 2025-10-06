package com.app.myappdeinventario.repository

import android.net.Uri
import com.app.myappdeinventario.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProductoRepo {

    private val db = FirebaseFirestore.getInstance()
    private val productoCollection = db.collection("producto")

    private val storage = FirebaseStorage.getInstance().reference
    suspend fun addProducto(producto: Producto){
        productoCollection.add(producto).await()
    }

    suspend fun uploadImages(uris: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        for (uri in uris) {
            val fileRef = storage.child("productos/${System.currentTimeMillis()}_${uri.lastPathSegment}.jpg")
            fileRef.putFile(uri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            urls.add(downloadUrl)
        }
        return urls
    }
    suspend fun getProducto(): List<Producto>{
        val querySnapshot = productoCollection.get().await()
        return querySnapshot.documents.mapNotNull {  it.toObject(Producto::class.java)}
    }


    suspend fun editProducto(productoId: String, producto: Producto) {
        productoCollection.document(productoId).set(producto).await()
    }


    suspend fun deleteProducto(productoId: String) {
        productoCollection.document(productoId).delete().await()
    }
}