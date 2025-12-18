package com.example.shoptimize.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.relations.ListaConProductos
import com.example.shoptimize.data.repository.ListaDeCompraRepository
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShoptimizeDatabase.getInstance(application)
    private val repository = ListaDeCompraRepository(
        database.listaDeCompraDao(),
        database.listaProductoCrossRefDao()
    )

    // Obtener listas archivadas en lugar de todas las listas
    private val _allPurchases: LiveData<List<ListaConProductos>> = 
        database.listaDeCompraDao().getListasArchivadas().asLiveData()

    private val _selectedMonth = MutableLiveData<Int>()
    private val _selectedYear = MutableLiveData<Int>()

    val filteredPurchases: LiveData<List<ListaConProductos>> = MediatorLiveData<List<ListaConProductos>>().apply {
        addSource(_allPurchases) { filterPurchases() }
        addSource(_selectedMonth) { filterPurchases() }
        addSource(_selectedYear) { filterPurchases() }
    }

    private fun MediatorLiveData<List<ListaConProductos>>.filterPurchases() {
        val all = _allPurchases.value ?: emptyList()
        val month = _selectedMonth.value
        val year = _selectedYear.value

        value = all.filter { purchase ->
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                calendar.time = dateFormat.parse(purchase.lista.fecha) ?: Date()
                val purchaseMonth = calendar.get(Calendar.MONTH) + 1
                val purchaseYear = calendar.get(Calendar.YEAR)

                (month == null || month == purchaseMonth) &&
                        (year == null || year == purchaseYear)
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setMonth(month: Int) {
        _selectedMonth.value = month
    }

    fun setYear(year: Int) {
        _selectedYear.value = year
    }

    fun getAvailableYears(): List<String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return (currentYear downTo currentYear - 5).map { it.toString() }
    }
}
