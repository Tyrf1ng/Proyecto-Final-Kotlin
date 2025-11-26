package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.Producto
import com.example.shoptimize.databinding.FragmentListaDetalleBinding
import com.example.shoptimize.databinding.ItemProductoBinding

class ListaDetalleFragment : Fragment() {

    private var _binding: FragmentListaDetalleBinding? = null
    private val binding get() = _binding!!
    private var listaIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listaIndex = arguments?.getInt("listaIndex") ?: 0

        val adapter = ProductoAdapter()
        binding.recyclerviewProductos.adapter = adapter

        binding.fabAddProducto.setOnClickListener {
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ProductoAdapter :
        ListAdapter<Producto, ProductoViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.nombre == newItem.nombre

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
            return ProductoViewHolder(ItemProductoBinding.bind(view))
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val producto = getItem(position)
            holder.nombre.text = producto.nombre
            holder.precio.text = "\$${producto.precio}"
            holder.categoria.text = producto.categoria
        }
    }

    class ProductoViewHolder(binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val nombre: TextView = binding.textProductoNombre
        val precio: TextView = binding.textProductoPrecio
        val categoria: TextView = binding.textProductoCategoria
    }
}
