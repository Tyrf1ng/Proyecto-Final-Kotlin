package com.example.shoptimize.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoptimize.data.ListaDeCompra
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel : ViewModel() {

    private val _allPurchases = MutableLiveData<List<ListaDeCompra>>().apply {
        value = generateSamplePurchases()
    }

    private val _selectedMonth = MutableLiveData<Int>()
    private val _selectedYear = MutableLiveData<Int>()

    val filteredPurchases: LiveData<List<ListaDeCompra>> = MediatorLiveData<List<ListaDeCompra>>().apply {
        addSource(_allPurchases) { filterPurchases() }
        addSource(_selectedMonth) { filterPurchases() }
        addSource(_selectedYear) { filterPurchases() }
    }

    private fun MediatorLiveData<List<ListaDeCompra>>.filterPurchases() {
        val all = _allPurchases.value ?: emptyList()
        val month = _selectedMonth.value
        val year = _selectedYear.value

        value = all.filter { purchase ->
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                calendar.time = dateFormat.parse(purchase.fecha) ?: Date()
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

    private fun generateSamplePurchases(): List<ListaDeCompra> {
        val purchases = mutableListOf<ListaDeCompra>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Generar compras de ejemplo para los últimos 6 meses
        repeat(20) { i ->
            calendar.add(Calendar.DAY_OF_MONTH, -7) // Una compra cada semana
            val fecha = dateFormat.format(calendar.time)
            purchases.add(
                ListaDeCompra(
                    nombre = "Compra ${i + 1}",
                    total = (50 + i * 25),
                    fecha = fecha
                )
            )
        }

        return purchases.reversed()
    }
}
