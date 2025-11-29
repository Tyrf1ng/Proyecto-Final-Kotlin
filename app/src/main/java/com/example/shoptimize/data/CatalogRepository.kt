package com.example.shoptimize.data

object CatalogRepository {
    fun getProductos(): List<Producto> {
        return listOf(
            Producto("Pan con chancho", 5000, "Panadería"),
            Producto("Arroz", 3000, "Granos"),
            Producto("Trigo", 2500, "Granos"),
            Producto("Leche", 2000, "Lácteos"),
            Producto("Papas", 1500, "Verduras"),
            Producto("Carne", 8000, "Carnes"),
            Producto("Milo", 4000, "Bebidas"),
            Producto("Azúcar", 1800, "Condimentos"),
            Producto("Café", 6000, "Bebidas")
        )
    }
}
