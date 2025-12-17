package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.CatalogRepository
import com.example.shoptimize.data.Producto
import com.example.shoptimize.databinding.FragmentSeleccionarProductoBinding
import com.example.shoptimize.databinding.ItemCatalogoProductoBinding
import com.example.shoptimize.ui.transform.TransformViewModel

class SeleccionarProductoFragment : Fragment() {

    private var _binding: FragmentSeleccionarProductoBinding? = null
    private val binding get() = _binding!!
    private var listaIndex: Int = 0
    private var transformViewModel: TransformViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeleccionarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaIndex = arguments?.getInt("listaIndex") ?: 0
        transformViewModel = ViewModelProvider(requireActivity()).get(TransformViewModel::class.java)

        val adapter = SeleccionarProductoAdapter { producto ->
            agregarProductoALista(producto)
        }
        binding.recyclerviewCatalogoSeleccionar.adapter = adapter

        val productos = CatalogRepository.getProductos()
        adapter.submitList(productos)

        binding.searchCatalogo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun agregarProductoALista(producto: Producto) {
        transformViewModel?.listas?.value?.let { listas ->
            if (listaIndex >= 0 && listaIndex < listas.size) {
                val lista = listas[listaIndex]
                val productosActuales = lista.productos.toMutableList()
                productosActuales.add(producto)
                transformViewModel?.updateListaProductos(listaIndex, productosActuales)
            }
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class SeleccionarProductoAdapter(private val onProductoClick: (Producto) -> Unit) :
        ListAdapter<Producto, SeleccionarProductoViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.nombre == newItem.nombre

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeleccionarProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catalogo_producto, parent, false)
            return SeleccionarProductoViewHolder(ItemCatalogoProductoBinding.bind(view), onProductoClick)
        }

        override fun onBindViewHolder(holder: SeleccionarProductoViewHolder, position: Int) {
            val producto = getItem(position)
            holder.bind(producto)
        }
    }

    class SeleccionarProductoViewHolder(
        binding: ItemCatalogoProductoBinding,
        private val onProductoClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val nombre: TextView = binding.textCatalogoNombre
        private val precio: TextView = binding.textCatalogoPrecio
        private val categoria: TextView = binding.textCatalogoCategoria
        private val btnAgregar = binding.btnAgregar

        fun bind(producto: Producto) {
            nombre.text = producto.nombre
            precio.text = "\$${producto.precio}"
            categoria.text = producto.categoria
            btnAgregar.setOnClickListener {
                onProductoClick(producto)
            }
        }
    }
}
