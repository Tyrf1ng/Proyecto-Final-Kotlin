package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.CatalogRepository
import com.example.shoptimize.data.Producto
import com.example.shoptimize.databinding.FragmentAgregarProductoBinding
import com.example.shoptimize.databinding.ItemCatalogoProductoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AgregarProductoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!
    private var onProductoSeleccionado: ((Producto) -> Unit)? = null

    fun setOnProductoSeleccionado(callback: (Producto) -> Unit) {
        onProductoSeleccionado = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductoSheetAdapter { producto ->
            onProductoSeleccionado?.invoke(producto)
            dismiss()
        }
        binding.recyclerviewProductosSheet.adapter = adapter

        val productos = CatalogRepository.getProductos()
        adapter.submitList(productos)

        binding.searchProductosSheet.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrBlank()) {
                    productos
                } else {
                    productos.filter {
                        it.nombre.contains(newText, ignoreCase = true) ||
                        it.categoria.contains(newText, ignoreCase = true)
                    }
                }
                adapter.submitList(filtered)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ProductoSheetAdapter(private val onItemClick: (Producto) -> Unit) :
        ListAdapter<Producto, ProductoSheetViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.nombre == newItem.nombre

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoSheetViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catalogo_producto, parent, false)
            return ProductoSheetViewHolder(ItemCatalogoProductoBinding.bind(view), onItemClick)
        }

        override fun onBindViewHolder(holder: ProductoSheetViewHolder, position: Int) {
            val producto = getItem(position)
            holder.nombre.text = producto.nombre
            holder.precio.text = "\$${producto.precio}"
            holder.categoria.text = producto.categoria
            holder.producto = producto
        }
    }

    class ProductoSheetViewHolder(
        binding: ItemCatalogoProductoBinding,
        private val onItemClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        val nombre: TextView = binding.textCatalogoNombre
        val precio: TextView = binding.textCatalogoPrecio
        val categoria: TextView = binding.textCatalogoCategoria
        private val btnAgregar = binding.btnAgregar
        var producto: Producto? = null

        init {
            btnAgregar.setOnClickListener {
                producto?.let { onItemClick(it) }
            }
        }
    }
}
