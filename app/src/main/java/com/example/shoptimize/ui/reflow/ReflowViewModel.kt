package com.example.shoptimize.ui.reflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.repository.ProductoRepository
import kotlinx.coroutines.launch

class ReflowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShoptimizeDatabase.getInstance(application)
    private val repository = ProductoRepository(database.productoDao())

    val allProductos: LiveData<List<Producto>> = repository.allProductos.asLiveData()
    
    fun searchProductos(query: String): LiveData<List<Producto>> {
        return repository.searchProductos(query).asLiveData()
    }
    
    fun addProducto(producto: Producto) {
        viewModelScope.launch {
            repository.insertProducto(producto)
        }
    }
    
    fun deleteProducto(producto: Producto) {
        viewModelScope.launch {
            repository.deleteProducto(producto)
        }
    }
    
    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            repository.updateProducto(producto)
        }
    }
}