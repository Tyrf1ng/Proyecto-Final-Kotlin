package com.example.shoptimize.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.model.Producto

class ProductsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.products_recycler_view)
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)

        // Ejemplo de productos con nombres como en la imagen
        val productos = listOf(
            Producto("Fideos", android.R.drawable.ic_menu_gallery),
            Producto("Salsa de tomate", android.R.drawable.ic_menu_gallery),
            Producto("Arroz", android.R.drawable.ic_menu_gallery),
            Producto("Vino", android.R.drawable.ic_menu_gallery),
            Producto("Harina", android.R.drawable.ic_menu_gallery),
            Producto("Pan", android.R.drawable.ic_menu_gallery)
        )

        val productosFiltrados = productos.toMutableList()
        val adapter = ProductsAdapter(productosFiltrados)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter

        // Filtrado en tiempo real
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                productosFiltrados.clear()
                if (query.isEmpty()) {
                    productosFiltrados.addAll(productos)
                } else {
                    productosFiltrados.addAll(productos.filter {
                        it.nombre.lowercase().contains(query)
                    })
                }
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        return view
    }
}
