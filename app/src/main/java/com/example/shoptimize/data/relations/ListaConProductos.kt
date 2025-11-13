package com.example.shoptimize.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shoptimize.data.entities.ListaDeCompras
import com.example.shoptimize.data.entities.Producto

data class ListaConProductos(
    @Embedded val lista: ListaDeCompras,
    @Relation(
        parentColumn = "id",
        entityColumn = "lista_id"
    )
    val productos: List<Producto>
)