package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    @Ignore
    val compras: MutableList<ListaDeCompra> = mutableListOf()
) {
    // Constructor secundario para Room (sin el campo @Ignore)
    constructor(id: Int, nombre: String) : this(id, nombre, mutableListOf())
}
