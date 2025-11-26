package com.example.shoptimize.data

data class Historial(
    val listaDeCompras: MutableList<ListaDeCompra> = mutableListOf(),
    val mesAno: String
) {
    val total: Int
        get() = calculateTotal()
    
    fun compararMesPasado(): Int {
        return 0
    }
    
    fun calculateTotal(): Int {
        return listaDeCompras.sumOf { it.calculateTotal() }
    }
}
