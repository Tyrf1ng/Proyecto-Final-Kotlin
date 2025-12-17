package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    var precio: Int,
    val categoria: String
) {
    fun setearPrecio(nuevoPrecio: Int) {
        precio = nuevoPrecio
    }
}
