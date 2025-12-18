package com.example.shoptimize.ui.reflow

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import coil.load
import com.google.android.material.imageview.ShapeableImageView

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
                // Click en el producto para editar
                mostrarDialogoEditarProducto(producto, reflowViewModel)
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
            
            binding.searchProductos.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val filtered = if (s.isNullOrBlank()) {
                        productos
                    } else {
                        productos.filter {
                            it.nombre.contains(s, ignoreCase = true) ||
                            it.categoria.contains(s, ignoreCase = true)
                        }
                    }
                    adapter.submitList(filtered)
                }
                
                override fun afterTextChanged(s: Editable?) {}
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
        
        // Campo nombre con TextInputLayout
        val tilNombre = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Nombre del producto"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        }
        val inputNombre = com.google.android.material.textfield.TextInputEditText(tilNombre.context)
        tilNombre.addView(inputNombre)
        
        // Campo precio con TextInputLayout
        val tilPrecio = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Precio"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputPrecio = com.google.android.material.textfield.TextInputEditText(tilPrecio.context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        tilPrecio.addView(inputPrecio)
        
        // Campo categoría con TextInputLayout
        val tilCategoria = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Categoría"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputCategoria = com.google.android.material.textfield.TextInputEditText(tilCategoria.context)
        tilCategoria.addView(inputCategoria)
        
        // Campo URL con TextInputLayout
        val tilImagenUrl = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "URL de la imagen (opcional)"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputImagenUrl = com.google.android.material.textfield.TextInputEditText(tilImagenUrl.context).apply {
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_URI
        }
        tilImagenUrl.addView(inputImagenUrl)
        
        layout.addView(tilNombre)
        layout.addView(tilPrecio)
        layout.addView(tilCategoria)
        layout.addView(tilImagenUrl)
        
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
                            categoria = categoria.ifBlank { "General" },
                            imagenUrl = inputImagenUrl.text.toString().takeIf { it.isNotBlank() }
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
    
    private fun mostrarDialogoEditarProducto(producto: Producto, reflowViewModel: ReflowViewModel) {
        // Crear un layout vertical con campos de entrada
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        // Campo nombre con TextInputLayout
        val tilNombre = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Nombre del producto"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        }
        val inputNombre = com.google.android.material.textfield.TextInputEditText(tilNombre.context).apply {
            setText(producto.nombre)
        }
        tilNombre.addView(inputNombre)
        
        // Campo precio con TextInputLayout
        val tilPrecio = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Precio"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputPrecio = com.google.android.material.textfield.TextInputEditText(tilPrecio.context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(producto.precio.toString())
        }
        tilPrecio.addView(inputPrecio)
        
        // Campo categoría con TextInputLayout
        val tilCategoria = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Categoría"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputCategoria = com.google.android.material.textfield.TextInputEditText(tilCategoria.context).apply {
            setText(producto.categoria)
        }
        tilCategoria.addView(inputCategoria)
        
        // Campo URL con TextInputLayout
        val tilImagenUrl = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "URL de la imagen (opcional)"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
        val inputImagenUrl = com.google.android.material.textfield.TextInputEditText(tilImagenUrl.context).apply {
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_URI
            setText(producto.imagenUrl ?: "")
        }
        tilImagenUrl.addView(inputImagenUrl)
        
        layout.addView(tilNombre)
        layout.addView(tilPrecio)
        layout.addView(tilCategoria)
        layout.addView(tilImagenUrl)
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Editar Producto")
        builder.setView(layout)
        
        builder.setPositiveButton("Guardar") { _, _ ->
            val nombre = inputNombre.text.toString()
            val precio = inputPrecio.text.toString().toIntOrNull() ?: producto.precio
            val categoria = inputCategoria.text.toString()
            val imagenUrl = inputImagenUrl.text.toString().takeIf { it.isNotBlank() }
            
            if (nombre.isNotBlank()) {
                val productoActualizado = producto.copy(
                    nombre = nombre,
                    precio = precio,
                    categoria = categoria.ifBlank { "General" },
                    imagenUrl = imagenUrl
                )
                
                reflowViewModel.updateProducto(productoActualizado)
                
                android.widget.Toast.makeText(
                    requireContext(),
                    "Producto actualizado",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
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
        private val imagen: ShapeableImageView = itemView.findViewById(R.id.image_catalogo)

        fun bind(producto: Producto) {
            nombre.text = producto.nombre
            precio.text = "\$${producto.precio}"
            categoria.text = producto.categoria
            
            // Cargar imagen desde URL con Coil
            imagen.load(producto.imagenUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_gallery)
            }
            itemView.setOnClickListener {
                onProductoClick(producto)
            }
            btnEliminar.setOnClickListener {
                onEliminarClick(producto)
            }
        }
    }
}