package com.app.myappdeinventario.model

import java.util.Date


data class Producto(
    val idProduct: String = "",
    val nombre: String = "",
    val precioC: Double = 0.0,
    val precioV: Double = 0.0,
    val precioPromocion: Double = 0.0,
    val descripcion: String = "",
    val stock: Int = 0,
    val image:  List<String> = emptyList(),
    val fechaCreacion: String = "",
    val idCategory: String = ""

)
