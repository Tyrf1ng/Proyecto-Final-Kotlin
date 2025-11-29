package com.example.shoptimize.data

import java.util.UUID

data class ListaDeCompra(
    val nombre: String,
    val total: Int = 0,
    val fecha: String,
    val productos: MutableList<Producto> = mutableListOf(),
    val id: String = UUID.randomUUID().toString()
) {
    fun createProduct(): Producto {
        return Producto("", 0, "")
    }
    
    fun addProduct(producto: Producto) {
        productos.add(producto)
    }
    
    fun calculateTotal(): Int {
        return productos.sumOf { it.precio }
    }
}
