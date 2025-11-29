package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.Producto
import com.example.shoptimize.databinding.FragmentListaDetalleBinding
import com.example.shoptimize.databinding.ItemProductoBinding
import com.example.shoptimize.ui.transform.TransformViewModel

class ListaDetalleFragment : Fragment() {

    private var _binding: FragmentListaDetalleBinding? = null
    private val binding get() = _binding!!
    private var listaIndex: Int = 0
    private val productosAgregados = mutableListOf<Producto>()
    private var transformViewModel: TransformViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listaIndex = arguments?.getInt("listaIndex") ?: 0
        transformViewModel = ViewModelProvider(requireActivity()).get(TransformViewModel::class.java)

        val adapter = ProductoAdapter()
        binding.recyclerviewProductos.adapter = adapter

        adapter.setOnDeleteListener { position ->
            if (position >= 0 && position < productosAgregados.size) {
                productosAgregados.removeAt(position)
                transformViewModel?.updateListaProductos(listaIndex, productosAgregados.toList())
            }
        }

        transformViewModel?.listas?.observe(viewLifecycleOwner) { listas ->
            if (listaIndex < listas.size) {
                val lista = listas[listaIndex]
                productosAgregados.clear()
                productosAgregados.addAll(lista.productos)
                adapter.submitList(productosAgregados.toList())
                val total = lista.calculateTotal()
                binding.textTotalLista.text = "\$$total"
            }
        }

        binding.fabAddProducto.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("listaIndex", listaIndex)
            }
            findNavController().navigate(R.id.action_lista_detalle_to_seleccionar_producto, bundle)
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

        private var onDeleteListener: ((Int) -> Unit)? = null

        fun setOnDeleteListener(listener: (Int) -> Unit) {
            onDeleteListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
            return ProductoViewHolder(ItemProductoBinding.bind(view))
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val producto = getItem(position)
            holder.nombre.text = producto.nombre
            holder.precio.text = "\$${producto.precio}"
            holder.categoria.text = producto.categoria
            holder.btnEliminar.setOnClickListener {
                onDeleteListener?.invoke(position)
            }
        }
    }

    class ProductoViewHolder(binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val nombre: TextView = binding.textProductoNombre
        val precio: TextView = binding.textProductoPrecio
        val categoria: TextView = binding.textProductoCategoria
        val btnEliminar: ImageView = binding.btnEliminar
    }
}
