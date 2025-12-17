package com.example.shoptimize.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.shoptimize.data.ListaDeCompra
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.ListaProductoCrossRef

data class ListaConProductos(
    @Embedded var lista: ListaDeCompra,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ListaProductoCrossRef::class,
            parentColumn = "listaId",
            entityColumn = "productoId"
        )
    )
    val productos: List<Producto>
) {
    fun calculateTotal(): Int {
        return lista.total
    }
    
    fun addProduct(producto: Producto) {
        calculateTotal()
    }
}