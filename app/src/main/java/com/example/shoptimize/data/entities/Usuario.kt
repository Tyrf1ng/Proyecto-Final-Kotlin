package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nombre: String,
    val avatarResId: Int = 0
) {
    fun changeName(nuevoNombre: String) {
        nombre = nuevoNombre
    }
}
