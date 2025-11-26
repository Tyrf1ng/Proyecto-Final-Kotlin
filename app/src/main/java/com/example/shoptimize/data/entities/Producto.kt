package com.example.shoptimize.data

data class Producto(
    val nombre: String,
    val precio: Int,
    val categoria: String
) {
    fun setearPrecio(): Int {
        return precio
    }
}
