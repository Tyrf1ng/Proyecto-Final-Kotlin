package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.repository.ListaDeCompraRepository
import com.example.shoptimize.databinding.FragmentSeleccionarProductoBinding
import com.example.shoptimize.databinding.ItemProductoSeleccionarBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SeleccionarProductoFragment : Fragment() {

    private var _binding: FragmentSeleccionarProductoBinding? = null
    private val binding get() = _binding!!
    private var listaId: Int = 0
    private lateinit var repository: ListaDeCompraRepository
    private var allProductos: List<Producto> = emptyList()

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

        listaId = arguments?.getInt("listaId") ?: 0
        
        val database = ShoptimizeDatabase.getInstance(requireContext())
        repository = ListaDeCompraRepository(
            database.listaDeCompraDao(),
            database.listaProductoCrossRefDao()
        )

        val adapter = SeleccionarProductoAdapter { producto, cantidad ->
            agregarProductoALista(producto, cantidad)
        }
        binding.recyclerviewCatalogoSeleccionar.adapter = adapter

        // Cargar productos
        lifecycleScope.launch {
            allProductos = database.productoDao().getAllProductos().first()
            adapter.submitList(allProductos)
        }

        binding.searchCatalogo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrBlank()) {
                    allProductos
                } else {
                    allProductos.filter {
                        it.nombre.contains(newText, ignoreCase = true) ||
                        it.categoria.contains(newText, ignoreCase = true)
                    }
                }
                adapter.submitList(filtered)
                return true
            }
        })
    }

    private fun agregarProductoALista(producto: Producto, cantidad: Int) {
        lifecycleScope.launch {
            repository.addProductoToLista(listaId, producto.id, cantidad)
            android.widget.Toast.makeText(
                requireContext(),
                "Agregado: ${producto.nombre} x$cantidad",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class SeleccionarProductoAdapter(private val onProductoClick: (Producto, Int) -> Unit) :
        ListAdapter<Producto, SeleccionarProductoViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeleccionarProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto_seleccionar, parent, false)
            return SeleccionarProductoViewHolder(ItemProductoSeleccionarBinding.bind(view), onProductoClick)
        }

        override fun onBindViewHolder(holder: SeleccionarProductoViewHolder, position: Int) {
            val producto = getItem(position)
            holder.bind(producto)
        }
    }

    class SeleccionarProductoViewHolder(
        binding: ItemProductoSeleccionarBinding,
        private val onProductoClick: (Producto, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val nombre: TextView = binding.textCatalogoNombre
        private val precio: TextView = binding.textCatalogoPrecio
        private val categoria: TextView = binding.textCatalogoCategoria
        
        private val btnDecrease: MaterialButton = binding.btnDecrease
        private val btnIncrease: MaterialButton = binding.btnIncrease
        private val editTextQuantity: TextInputEditText = binding.editTextQuantity
        private val btnAgregarConCantidad: MaterialButton = binding.btnAgregarConCantidad

        fun bind(producto: Producto) {
            nombre.text = producto.nombre
            precio.text = "\$${producto.precio}"
            categoria.text = producto.categoria
            editTextQuantity.setText("1")

            btnDecrease.setOnClickListener {
                updateQuantity(-1)
            }
            btnIncrease.setOnClickListener {
                updateQuantity(1)
            }
            btnAgregarConCantidad.setOnClickListener {
                val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1
                onProductoClick(producto, quantity)
            }
        }
        
        private fun updateQuantity(change: Int) {
            var quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1
            quantity += change
            if (quantity < 1) {
                quantity = 1
            }
            editTextQuantity.setText(quantity.toString())
        }
    }
}
