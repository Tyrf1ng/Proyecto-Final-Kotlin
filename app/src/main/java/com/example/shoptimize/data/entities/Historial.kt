package com.example.shoptimize.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.shoptimize.data.relations.ListaConProductos

@Entity(tableName = "historiales")
data class Historial(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mesAno: String,
    var total: Int = 0,
    @Ignore
    val listas: List<ListaConProductos> = emptyList()
) {
    constructor(id: Int, mesAno: String, total: Int) : this(id, mesAno, total, emptyList())
    
    fun calculateTotal(): Int {
        total = listas.sumOf { it.calculateTotal() }
        return total
    }
    
    fun compararMesPasado(historialAnterior: Historial?): Int {
        if (historialAnterior == null) return 0
        return calculateTotal() - historialAnterior.calculateTotal()
    }
}
