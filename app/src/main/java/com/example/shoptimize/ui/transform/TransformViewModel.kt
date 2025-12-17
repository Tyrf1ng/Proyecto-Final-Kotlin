package com.example.shoptimize.ui.transform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoptimize.data.ListaDeCompra
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.relations.ListaConProductos
import com.example.shoptimize.data.repository.ListaDeCompraRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransformViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShoptimizeDatabase.getInstance(application)
    private val repository = ListaDeCompraRepository(
        database.listaDeCompraDao(),
        database.listaProductoCrossRefDao()
    )

    val listasConProductos: LiveData<List<ListaConProductos>> = 
        repository.allListasConProductos.asLiveData()

    fun addLista(nombre: String, usuarioId: Int? = null) {
        viewModelScope.launch {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val nuevaLista = ListaDeCompra(
                nombre = nombre,
                fecha = fmt.format(Date()),
                usuarioId = usuarioId
            )
            repository.insertLista(nuevaLista)
        }
    }

    fun updateListaNombre(id: Int, nombre: String) {
        viewModelScope.launch {
            repository.updateListaNombre(id, nombre)
        }
    }

    fun deleteLista(lista: ListaDeCompra) {
        viewModelScope.launch {
            repository.deleteLista(lista)
        }
    }

    fun addProductoToLista(listaId: Int, productoId: Int, cantidad: Int = 1) {
        viewModelScope.launch {
            repository.addProductoToLista(listaId, productoId, cantidad)
        }
    }

    fun removeProductoFromLista(listaId: Int, productoId: Int) {
        viewModelScope.launch {
            repository.removeProductoFromLista(listaId, productoId)
        }
    }
}