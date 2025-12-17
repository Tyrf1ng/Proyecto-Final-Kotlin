package com.example.shoptimize.ui.transform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoptimize.data.ListaDeCompra
import com.example.shoptimize.data.Producto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransformViewModel : ViewModel() {

    private val _listas = MutableLiveData<MutableList<ListaDeCompra>>().apply {
        value = mutableListOf<ListaDeCompra>().also { list ->
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            repeat(6) { i ->
                val nuevaLista = ListaDeCompra(
                    nombre = "Lista ${i + 1}",
                    total = 0,
                    fecha = fmt.format(Date())
                )
                
                when (i) {
                    0 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Pan", 3000, "Panadería"),
                            Producto("Leche", 2500, "Lácteos")
                        ))
                    }
                    1 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Arroz", 5000, "Granos"),
                            Producto("Frijoles", 4500, "Granos"),
                            Producto("Aceite", 6000, "Condimentos")
                        ))
                    }
                    2 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Manzanas", 2000, "Frutas"),
                            Producto("Naranjas", 1800, "Frutas")
                        ))
                    }
                    3 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Pollo", 12000, "Carnes"),
                            Producto("Carne molida", 14000, "Carnes")
                        ))
                    }
                    4 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Lechuga", 3000, "Verduras"),
                            Producto("Tomates", 2500, "Verduras"),
                            Producto("Cebolla", 1500, "Verduras")
                        ))
                    }
                    5 -> {
                        nuevaLista.productos.addAll(listOf(
                            Producto("Queso", 8000, "Lácteos"),
                            Producto("Yogur", 3500, "Lácteos"),
                            Producto("Mantequilla", 7000, "Lácteos")
                        ))
                    }
                }
                list.add(nuevaLista)
            }
        }
    }

    val listas: LiveData<MutableList<ListaDeCompra>> = _listas

    fun addLista(nombre: String) {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val nueva = ListaDeCompra(nombre = nombre, fecha = fmt.format(Date()))
        val current = _listas.value ?: mutableListOf()
        current.add(0, nueva)
        _listas.value = current
    }

    fun updateListaProductos(index: Int, productos: List<com.example.shoptimize.data.Producto>) {
        val current = _listas.value ?: return
        if (index >= 0 && index < current.size) {
            val listaActualizada = current[index].copy(
                nombre = current[index].nombre,
                total = 0,
                fecha = current[index].fecha,
                productos = productos.toMutableList()
            )
            current[index] = listaActualizada
            val newList = mutableListOf<ListaDeCompra>()
            newList.addAll(current)
            _listas.value = newList
        }
    }
}