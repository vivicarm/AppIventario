package com.app.myappdeinventario.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.app.myappdeinventario.model.Usuario
class AuthRepository(

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    //logi de usuario
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //  Registro con datos adicionales
    suspend fun registro(nombre: String, apellido: String, genero: String, email: String, password: String): Result<Unit> {
        return try {

            // Crea cuenta en Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("No se pudo crear el usuario")

            // Guarda los datos adicionales en Firestore
            val usuario = Usuario(
                idUsuario = user.uid,
                nombre = nombre,
                apellido = apellido,
                genero = genero,
                email = email,
                password = password
            )

            db.collection("usuarios").document(user.uid).set(usuario).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerUsuarioActual(): Usuario? {
        val currentUser = firebaseAuth.currentUser ?: return null

        return try {
            val snapshot = db.collection("usuarios")
                .document(currentUser.uid)
                .get()
                .await()

            snapshot.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar perfil de usuario
    suspend fun actualizarPerfil(nombre: String, email: String): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No hay usuario autenticado")

            // Actualizar email en Firebase Auth si cambió
            if (currentUser.email != email) {
                currentUser.updateEmail(email).await()
            }

            // Actualizar datos en Firestore
            val updates = hashMapOf<String, Any>(
                "nombre" to nombre,
                "email" to email
            )

            db.collection("usuarios").document(currentUser.uid).update(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //cerrar sesio
    fun logout() {
        firebaseAuth.signOut()
    }

    //obtiene el usuario actual
    fun currentUser() = firebaseAuth.currentUser

}