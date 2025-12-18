package com.example.shoptimize.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.databinding.HistoryListsBinding

class HistoryFragment : Fragment() {

    private var _binding: HistoryListsBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = HistoryListsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupDropdowns()
        setupRecyclerView()

        return root
    }

    private fun setupDropdowns() {
        // Configurar dropdown de meses
        val months = resources.getStringArray(R.array.months)
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months)
        (binding.monthDropdownLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(monthAdapter)
            setOnItemClickListener { _, _, position, _ ->
                historyViewModel.setMonth(position + 1)
            }
        }

        // Configurar dropdown de años
        val years = historyViewModel.getAvailableYears()
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, years)
        (binding.yearDropdownLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(yearAdapter)
            setOnItemClickListener { _, _, position, _ ->
                historyViewModel.setYear(years[position].toInt())
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.purchaseList.adapter = adapter
        
        adapter.setItemClickListener { listaId ->
            val bundle = Bundle().apply {
                putInt("listaId", listaId)
                putBoolean("readOnly", true)
            }
            findNavController().navigate(R.id.nav_lista_detalle, bundle)
        }

        historyViewModel.filteredPurchases.observe(viewLifecycleOwner) { purchases ->
            adapter.submitList(purchases)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
