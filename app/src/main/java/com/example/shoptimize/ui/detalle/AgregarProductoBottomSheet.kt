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
import com.example.shoptimize.databinding.ItemProductoSeleccionarBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText

class AgregarProductoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentAgregarProductoBinding? = null
    private val binding get() = _binding!!
    private var onProductoSeleccionado: ((Producto, Int) -> Unit)? = null

    fun setOnProductoSeleccionado(callback: (Producto, Int) -> Unit) {
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

        val adapter = ProductoSheetAdapter { producto, cantidad ->
            onProductoSeleccionado?.invoke(producto, cantidad)
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

    class ProductoSheetAdapter(private val onItemClick: (Producto, Int) -> Unit) :
        ListAdapter<Producto, ProductoSheetViewHolder>(object : DiffUtil.ItemCallback<Producto>() {

            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem.nombre == newItem.nombre

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoSheetViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto_seleccionar, parent, false)
            return ProductoSheetViewHolder(ItemProductoSeleccionarBinding.bind(view), onItemClick)
        }

        override fun onBindViewHolder(holder: ProductoSheetViewHolder, position: Int) {
            val producto = getItem(position)
            holder.bind(producto)
        }
    }

    class ProductoSheetViewHolder(
        binding: ItemProductoSeleccionarBinding,
        private val onItemClick: (Producto, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val nombre: TextView = binding.textCatalogoNombre
        private val precio: TextView = binding.textCatalogoPrecio
        private val categoria: TextView = binding.textCatalogoCategoria
        private val imagen: ShapeableImageView = binding.imageItem
        private val btnDecrease: MaterialButton = binding.btnDecrease
        private val btnIncrease: MaterialButton = binding.btnIncrease
        private val editTextQuantity: TextInputEditText = binding.editTextQuantity
        private val btnAgregar: MaterialButton = binding.btnAgregarConCantidad
        private var producto: Producto? = null

        init {
            btnDecrease.setOnClickListener {
                updateQuantity(-1)
            }
            btnIncrease.setOnClickListener {
                updateQuantity(1)
            }
            btnAgregar.setOnClickListener {
                producto?.let {
                    val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 1
                    onItemClick(it, quantity)
                }
            }
        }

        fun bind(producto: Producto) {
            this.producto = producto
            nombre.text = producto.nombre
            precio.text = "$${producto.precio}"
            categoria.text = producto.categoria
            editTextQuantity.setText("1")

            imagen.load(producto.imagenUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_gallery)
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