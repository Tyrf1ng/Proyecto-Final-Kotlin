package com.example.shoptimize.ui.reflow

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.databinding.FragmentReflowBinding
import kotlinx.coroutines.launch

class ReflowFragment : Fragment() {

    private var _binding: FragmentReflowBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val reflowViewModel = ViewModelProvider(this).get(ReflowViewModel::class.java)
        val adapter = CatalogoAdapter(
            onProductoClick = { producto ->
                // Click en el producto para ver detalles
                android.widget.Toast.makeText(
                    requireContext(),
                    producto.nombre,
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            onEliminarClick = { producto ->
                // Confirmar eliminación
                mostrarDialogoConfirmarEliminacion(producto, reflowViewModel)
            }
        )
        
        binding.recyclerviewCatalogo.adapter = adapter

        // Observar productos de la base de datos
        reflowViewModel.allProductos.observe(viewLifecycleOwner) { productos ->
            adapter.submitList(productos)
            
            binding.searchProductos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        binding.fabAddProduct.setOnClickListener {
            mostrarDialogoAgregarProductoCatalogo(reflowViewModel)
        }

        return root
    }

    private fun mostrarDialogoAgregarProductoCatalogo(reflowViewModel: ReflowViewModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.simple_list_item_1, null
        )
        
        // Crear un layout vertical con campos de entrada
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val inputNombre = android.widget.EditText(requireContext()).apply {
            hint = "Nombre del producto"
        }
        
        val inputPrecio = android.widget.EditText(requireContext()).apply {
            hint = "Precio"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        
        val inputCategoria = android.widget.EditText(requireContext()).apply {
            hint = "Categoría"
        }
        
        layout.addView(inputNombre)
        layout.addView(inputPrecio)
        layout.addView(inputCategoria)
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Agregar Producto al Catálogo")
        builder.setView(layout)
        
        builder.setPositiveButton("Guardar") { _, _ ->
            val nombre = inputNombre.text.toString()
            val precio = inputPrecio.text.toString().toIntOrNull() ?: 0
            val categoria = inputCategoria.text.toString()
            
            if (nombre.isNotBlank()) {
                // Verificar si el producto ya existe
                lifecycleScope.launch {
                    val database = ShoptimizeDatabase.getInstance(requireContext())
                    val productoExistente = database.productoDao().getProductoByNombre(nombre)
                    
                    if (productoExistente != null) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Este producto ya existe en el catálogo",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val nuevoProducto = Producto(
                            nombre = nombre,
                            precio = precio,
                            categoria = categoria.ifBlank { "General" }
                        )
                        
                        reflowViewModel.addProducto(nuevoProducto)
                        
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Producto agregado al catálogo",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                android.widget.Toast.makeText(
                    requireContext(),
                    "El nombre no puede estar vacío",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoConfirmarEliminacion(producto: Producto, reflowViewModel: ReflowViewModel) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Producto")
        builder.setMessage("¿Está seguro de eliminar '${producto.nombre}' del catálogo?")
        
        builder.setPositiveButton("Eliminar") { _, _ ->
            reflowViewModel.deleteProducto(producto)
            android.widget.Toast.makeText(
                requireContext(),
                "Producto eliminado",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CatalogoAdapter(
        private val onProductoClick: (Producto) -> Unit,
        private val onEliminarClick: (Producto) -> Unit
    ) : ListAdapter<Producto, CatalogoViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catalogo_producto, parent, false)
            return CatalogoViewHolder(view, onProductoClick, onEliminarClick)
        }

        override fun onBindViewHolder(holder: CatalogoViewHolder, position: Int) {
            val producto = getItem(position)
            holder.bind(producto)
        }
    }

    class CatalogoViewHolder(
        itemView: View,
        private val onProductoClick: (Producto) -> Unit,
        private val onEliminarClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val nombre: TextView = itemView.findViewById(R.id.text_catalogo_nombre)
        private val precio: TextView = itemView.findViewById(R.id.text_catalogo_precio)
        private val categoria: TextView = itemView.findViewById(R.id.text_catalogo_categoria)
        private val btnEliminar = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_eliminar)

        fun bind(producto: Producto) {
            nombre.text = producto.nombre
            precio.text = "\$${producto.precio}"
            categoria.text = producto.categoria
            itemView.setOnClickListener {
                onProductoClick(producto)
            }
            btnEliminar.setOnClickListener {
                onEliminarClick(producto)
            }
        }
    }
}