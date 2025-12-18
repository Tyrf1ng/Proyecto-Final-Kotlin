package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "listas_de_compra",
    indices = [Index(value = ["usuarioId"])]
)
data class ListaDeCompra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    var total: Int = 0,
    val fecha: String,
    val usuarioId: Int? = null,
    val esArchivada: Boolean = false
) {
    fun createProduct(): Producto {
        return Producto(nombre = "", precio = 0, categoria = "")
    }
    
    fun calculateTotal(productos: List<Producto>): Int {
        total = productos.sumOf { it.precio }
        return total
    }
}
