package com.example.shoptimize.data

data class Usuario(
    val nombre: String,
    val compras: MutableList<ListaDeCompra> = mutableListOf()
)
