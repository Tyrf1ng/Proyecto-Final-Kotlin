package com.example.shoptimize.data

import com.example.shoptimize.data.repository.ProductoRepository

class CatalogRepository(private val productoRepository: ProductoRepository) {
    
    // Obtener productos del repositorio
    val allProductos = productoRepository.allProductos
    
    fun searchProductos(query: String) = productoRepository.searchProductos(query)
    
    fun getProductosByCategoria(categoria: String) = productoRepository.getProductosByCategoria(categoria)
    
    companion object {
        fun getProductos(): List<Producto> {
            return listOf(
                Producto(nombre = "Pan", precio = 3000, categoria = "Panadería"),
                Producto(nombre = "Arroz", precio = 3000, categoria = "Granos"),
                Producto(nombre = "Trigo", precio = 2500, categoria = "Granos"),
                Producto(nombre = "Leche", precio = 2000, categoria = "Lácteos"),
                Producto(nombre = "Papas", precio = 1500, categoria = "Verduras"),
                Producto(nombre = "Carne", precio = 8000, categoria = "Carnes"),
                Producto(nombre = "Milo", precio = 4000, categoria = "Bebidas"),
                Producto(nombre = "Azúcar", precio = 1800, categoria = "Condimentos"),
                Producto(nombre = "Café", precio = 6000, categoria = "Bebidas")
            )
        }
    }
}
