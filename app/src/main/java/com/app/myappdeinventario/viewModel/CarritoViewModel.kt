package com.app.myappdeinventario.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.myappdeinventario.model.CarritoItem
import com.app.myappdeinventario.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val _itemsCarrito = MutableStateFlow<List<CarritoItem>>(emptyList())
    val itemsCarrito: StateFlow<List<CarritoItem>> = _itemsCarrito

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    private val _totalPrecio = MutableStateFlow(0.0)
    val totalPrecio: StateFlow<Double> = _totalPrecio

    private val CART_KEY = "carrito_items"

    init {
        // Calcular totales cuando cambie la lista
        viewModelScope.launch {
            _itemsCarrito.collect { items ->
                _totalItems.value = items.sumOf { it.cantidad }
                _totalPrecio.value = items.sumOf { it.subtotal }
            }
        }
    }

    // Métodos para manejar persistencia (se llamarán desde Activities)
    fun cargarCarritoDesdeStorage(context: Context) {
        try {
            val sharedPrefs = context.getSharedPreferences("carrito_prefs", Context.MODE_PRIVATE)
            val json = sharedPrefs.getString(CART_KEY, null)
            if (json != null) {
                // Usar org.json para parsear JSON simple
                val jsonArray = org.json.JSONArray(json)
                val items = mutableListOf<CarritoItem>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val productoJson = jsonObject.getJSONObject("producto")
                    val producto = Producto(
                        idProduct = productoJson.getString("idProduct"),
                        nombre = productoJson.getString("nombre"),
                        precioC = productoJson.getDouble("precioC"),
                        precioV = productoJson.getDouble("precioV"),
                        precioPromocion = productoJson.getDouble("precioPromocion"),
                        descripcion = productoJson.getString("descripcion"),
                        stock = productoJson.getInt("stock"),
                        image = productoJson.getJSONArray("image").let { imgArray ->
                            List(imgArray.length()) { imgArray.getString(it) }
                        },
                        fechaCreacion = productoJson.getString("fechaCreacion"),
                        idCategory = productoJson.getString("idCategory")
                    )
                    val cantidad = jsonObject.getInt("cantidad")
                    items.add(CarritoItem(producto, cantidad))
                }
                _itemsCarrito.value = items
            }
        } catch (e: Exception) {
            // Si hay error al cargar, mantener lista vacía
            _itemsCarrito.value = emptyList()
        }
    }

    private fun guardarCarritoEnStorage(context: Context, items: List<CarritoItem>) {
        try {
            val sharedPrefs = context.getSharedPreferences("carrito_prefs", Context.MODE_PRIVATE)
            val jsonArray = org.json.JSONArray()
            for (item in items) {
                val jsonObject = org.json.JSONObject()
                val productoJson = org.json.JSONObject().apply {
                    put("idProduct", item.producto.idProduct)
                    put("nombre", item.producto.nombre)
                    put("precioC", item.producto.precioC)
                    put("precioV", item.producto.precioV)
                    put("precioPromocion", item.producto.precioPromocion)
                    put("descripcion", item.producto.descripcion)
                    put("stock", item.producto.stock)
                    put("image", org.json.JSONArray(item.producto.image))
                    put("fechaCreacion", item.producto.fechaCreacion)
                    put("idCategory", item.producto.idCategory)
                }
                jsonObject.put("producto", productoJson)
                jsonObject.put("cantidad", item.cantidad)
                jsonArray.put(jsonObject)
            }
            sharedPrefs.edit().putString(CART_KEY, jsonArray.toString()).apply()
        } catch (e: Exception) {
            // Si hay error al guardar, continuar sin guardar
        }
    }

    // Agregar producto al carrito
    fun agregarProducto(producto: Producto, cantidad: Int = 1, context: Context? = null) {
        val currentItems = _itemsCarrito.value.toMutableList()
        val existingItem = currentItems.find { it.producto.idProduct == producto.idProduct }

        if (existingItem != null) {
            // Si el producto ya existe, incrementar cantidad
            val updatedItem = existingItem.copy(cantidad = existingItem.cantidad + cantidad)
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = updatedItem
        } else {
            // Si no existe, agregar nuevo item
            currentItems.add(CarritoItem(producto, cantidad))
        }

        _itemsCarrito.value = currentItems

        // Guardar en SharedPreferences si tenemos contexto
        context?.let { guardarCarritoEnStorage(it, currentItems) }
    }

    // Remover producto del carrito
    fun removerProducto(productoId: String, context: Context? = null) {
        val currentItems = _itemsCarrito.value.toMutableList()
        currentItems.removeAll { it.producto.idProduct == productoId }
        _itemsCarrito.value = currentItems

        // Guardar en SharedPreferences si tenemos contexto
        context?.let { guardarCarritoEnStorage(it, currentItems) }
    }

    // Actualizar cantidad de un producto
    fun actualizarCantidad(productoId: String, nuevaCantidad: Int, context: Context? = null) {
        if (nuevaCantidad <= 0) {
            removerProducto(productoId, context)
            return
        }

        val currentItems = _itemsCarrito.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.producto.idProduct == productoId }

        if (itemIndex != -1) {
            val updatedItem = currentItems[itemIndex].copy(cantidad = nuevaCantidad)
            currentItems[itemIndex] = updatedItem
            _itemsCarrito.value = currentItems

            // Guardar en SharedPreferences si tenemos contexto
            context?.let { guardarCarritoEnStorage(it, currentItems) }
        }
    }

    // Limpiar carrito
    fun limpiarCarrito(context: Context? = null) {
        _itemsCarrito.value = emptyList()

        // Guardar en SharedPreferences si tenemos contexto
        context?.let { guardarCarritoEnStorage(it, emptyList()) }
    }

    // Verificar si un producto está en el carrito
    fun estaEnCarrito(productoId: String): Boolean {
        return _itemsCarrito.value.any { it.producto.idProduct == productoId }
    }

    // Obtener cantidad de un producto en el carrito
    fun obtenerCantidad(productoId: String): Int {
        return _itemsCarrito.value.find { it.producto.idProduct == productoId }?.cantidad ?: 0
    }
}