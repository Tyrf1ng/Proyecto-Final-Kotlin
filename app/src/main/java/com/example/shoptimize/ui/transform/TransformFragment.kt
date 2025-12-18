package com.example.shoptimize.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.databinding.FragmentTransformBinding
import com.example.shoptimize.databinding.ItemListaCompraBinding
import com.example.shoptimize.data.relations.ListaConProductos

class TransformFragment : Fragment() {

    private var _binding: FragmentTransformBinding? = null
    private val binding get() = _binding!!
    private lateinit var transformViewModel: TransformViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transformViewModel = ViewModelProvider(this).get(TransformViewModel::class.java)
        _binding = FragmentTransformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerviewTransform
        val adapter = TransformAdapter()
        recyclerView.adapter = adapter

        transformViewModel.listasConProductos.observe(viewLifecycleOwner) { listas ->
            adapter.submitList(listas)
        }

        adapter.setItemClickListener { listaId ->
            val bundle = Bundle().apply {
                putInt("listaId", listaId)
            }
            findNavController().navigate(R.id.nav_lista_detalle, bundle)
        }

        adapter.setDeleteListener { listaConProductos ->
            mostrarDialogoConfirmarEliminacion(listaConProductos)
        }

        binding.fabAddList?.setOnClickListener {
            showCreateDialog { name ->
                transformViewModel.addLista(name)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class TransformAdapter :
        ListAdapter<ListaConProductos, TransformViewHolder>(object : DiffUtil.ItemCallback<ListaConProductos>() {

            override fun areItemsTheSame(oldItem: ListaConProductos, newItem: ListaConProductos): Boolean =
                oldItem.lista.id == newItem.lista.id

            override fun areContentsTheSame(oldItem: ListaConProductos, newItem: ListaConProductos): Boolean {
                return oldItem.lista.nombre == newItem.lista.nombre && 
                       oldItem.lista.fecha == newItem.lista.fecha && 
                       oldItem.productos.size == newItem.productos.size &&
                       oldItem.calculateTotal() == newItem.calculateTotal()
            }
        }) {

        private val drawables = listOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6
        )
        private var onItemClickListener: ((Int) -> Unit)? = null
        private var onDeleteListener: ((ListaConProductos) -> Unit)? = null

        fun setItemClickListener(listener: (Int) -> Unit) {
            onItemClickListener = listener
        }

        fun setDeleteListener(listener: (ListaConProductos) -> Unit) {
            onDeleteListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_compra, parent, false)
            return TransformViewHolder(ItemListaCompraBinding.bind(view))
        }

        override fun onBindViewHolder(holder: TransformViewHolder, position: Int) {
            val listaConProductos = getItem(position)
            val lista = listaConProductos.lista
            holder.textView.text = lista.nombre
            holder.textView.isSelected = true
            holder.textFecha.text = lista.fecha
            holder.textTotal.text = "\$${lista.total}"
            val drawable = drawables[position % drawables.size]
            holder.imageView.setImageDrawable(
                ResourcesCompat.getDrawable(holder.imageView.resources, drawable, null)
            )
            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(lista.id)
            }
            holder.btnDelete.setOnClickListener {
                onDeleteListener?.invoke(listaConProductos)
            }
        }
    }

    class TransformViewHolder(binding: ItemListaCompraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.imageItem
        val textView: TextView = binding.textNombre
        val textFecha: TextView = binding.textFecha
        val textTotal: TextView = binding.textTotal
        val btnDelete: ImageView = binding.btnDeleteLista
    }

    private fun showCreateDialog(onCreate: (String) -> Unit) {
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }
        
        val textInputLayout = com.google.android.material.textfield.TextInputLayout(requireContext()).apply {
            hint = "Nombre de la lista"
            boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        }
        
        val editText = com.google.android.material.textfield.TextInputEditText(textInputLayout.context)
        
        textInputLayout.addView(editText)
        layout.addView(textInputLayout)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Nueva lista")
            .setView(layout)
            .setPositiveButton("Crear") { _, _ ->
                val name = editText.text.toString().ifBlank { "Nueva lista" }
                onCreate(name)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoConfirmarEliminacion(listaConProductos: ListaConProductos) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar lista")
            .setMessage("¿Estás seguro de que deseas eliminar la lista \"${listaConProductos.lista.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                transformViewModel.deleteLista(listaConProductos.lista)
                Toast.makeText(requireContext(), "Lista eliminada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
