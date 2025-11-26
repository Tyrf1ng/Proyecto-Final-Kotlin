package com.example.shoptimize.ui.transform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoptimize.data.ListaDeCompra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransformViewModel : ViewModel() {

    private val _listas = MutableLiveData<MutableList<ListaDeCompra>>().apply {
        value = mutableListOf<ListaDeCompra>().also { list ->
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            repeat(6) { i ->
                list.add(
                    ListaDeCompra(
                        nombre = "Lista ${i + 1}",
                        total = (i + 1) * 100,
                        fecha = fmt.format(Date())
                    )
                )
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
}