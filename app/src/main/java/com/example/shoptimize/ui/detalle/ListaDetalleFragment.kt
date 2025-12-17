package com.example.shoptimize.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.repository.ListaDeCompraRepository
import com.example.shoptimize.databinding.FragmentListaDetalleBinding
import com.example.shoptimize.databinding.ItemProductoBinding
import kotlinx.coroutines.launch

data class ProductoConCantidad(
    val producto: Producto,
    val cantidad: Int
)

class ListaDetalleFragment : Fragment() {

    private var _binding: FragmentListaDetalleBinding? = null
    private val binding get() = _binding!!
    private var listaId: Int = 0
    private lateinit var repository: ListaDeCompraRepository
    private lateinit var database: ShoptimizeDatabase
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listaId = arguments?.getInt("listaId") ?: 0
        
        database = ShoptimizeDatabase.getInstance(requireContext())
        repository = ListaDeCompraRepository(
            database.listaDeCompraDao(),
            database.listaProductoCrossRefDao()
        )

        adapter = ProductoAdapter()
        binding.recyclerviewProductos.adapter = adapter

        adapter.setOnDeleteListener { productoConCantidad ->
            lifecycleScope.launch {
                repository.removeProductoFromLista(listaId, productoConCantidad.producto.id)
                cargarProductos()
            }
        }

        // Cargar productos inicialmente
        lifecycleScope.launch {
            cargarProductos()
        }

        binding.fabAddProducto.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("listaId", listaId)
            }
            findNavController().navigate(R.id.action_lista_detalle_to_seleccionar_producto, bundle)
        }

        return root
    }

    private suspend fun cargarProductos() {
        // Obtener la lista con productos
        val listaConProductos = database.listaDeCompraDao().getListaConProductos(listaId)
        
        if (listaConProductos != null) {
            val crossRefs = database.listaProductoCrossRefDao().getCrossRefsByLista(listaId)
            
            val cantidadMap = crossRefs.associate { it.productoId to it.cantidad }
            
            // Mapear productos con su cantidad
            val productosConCantidad = listaConProductos.productos.map { producto ->
                ProductoConCantidad(
                    producto = producto,
                    cantidad = cantidadMap[producto.id] ?: 1
                )
            }
            
            adapter.submitList(productosConCantidad)
            
            // Calcular total sumando precio × cantidad
            val total = productosConCantidad.sumOf { it.producto.precio * it.cantidad }
            binding.textTotalLista.text = "\$$total"
                // Sincronizar total calculado en la base de datos para mantener la tarjeta en lista de compras coherente
                database.listaDeCompraDao().updateListaTotal(listaId, total)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ProductoAdapter :
        ListAdapter<ProductoConCantidad, ProductoViewHolder>(object : DiffUtil.ItemCallback<ProductoConCantidad>() {

            override fun areItemsTheSame(oldItem: ProductoConCantidad, newItem: ProductoConCantidad): Boolean =
                oldItem.producto.id == newItem.producto.id

            override fun areContentsTheSame(oldItem: ProductoConCantidad, newItem: ProductoConCantidad): Boolean =
                oldItem == newItem
        }) {

        private var onDeleteListener: ((ProductoConCantidad) -> Unit)? = null

        fun setOnDeleteListener(listener: (ProductoConCantidad) -> Unit) {
            onDeleteListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
            return ProductoViewHolder(ItemProductoBinding.bind(view))
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val item = getItem(position)
            val producto = item.producto
            val cantidad = item.cantidad
            val totalPrecio = producto.precio * cantidad
            
            holder.nombre.text = "${producto.nombre} x${cantidad}"
            holder.precio.text = "\$${totalPrecio}"
            holder.categoria.text = producto.categoria
            holder.btnEliminar.setOnClickListener {
                onDeleteListener?.invoke(item)
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
