package com.app.myappdeinventario.model

data class CarritoItem(
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Double
        get() = producto.precioV * cantidad
}