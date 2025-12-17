package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "lista_producto_cross_ref",
    primaryKeys = ["listaId", "productoId"],
    foreignKeys = [
        ForeignKey(
            entity = ListaDeCompra::class,
            parentColumns = ["id"],
            childColumns = ["listaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listaId"]), Index(value = ["productoId"])]
)
data class ListaProductoCrossRef(
    val listaId: Int,
    val productoId: Int,
    val cantidad: Int = 1
)
