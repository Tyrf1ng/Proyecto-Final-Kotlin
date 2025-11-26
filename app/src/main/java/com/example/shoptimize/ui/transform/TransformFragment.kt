package com.example.shoptimize.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.databinding.FragmentTransformBinding
import com.example.shoptimize.databinding.ItemListaCompraBinding

class TransformFragment : Fragment() {

    private var _binding: FragmentTransformBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transformViewModel = ViewModelProvider(this).get(TransformViewModel::class.java)
        _binding = FragmentTransformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerviewTransform
        val adapter = TransformAdapter()
        recyclerView.adapter = adapter

        transformViewModel.listas.observe(viewLifecycleOwner) { listas ->
            adapter.submitList(listOfItemsFrom(listas))
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
        ListAdapter<ListaItem, TransformViewHolder>(object : DiffUtil.ItemCallback<ListaItem>() {

            override fun areItemsTheSame(oldItem: ListaItem, newItem: ListaItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListaItem, newItem: ListaItem): Boolean =
                oldItem == newItem
        }) {

        private val drawables = listOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_compra, parent, false)
            return TransformViewHolder(ItemListaCompraBinding.bind(view))
        }

        override fun onBindViewHolder(holder: TransformViewHolder, position: Int) {
            val item = getItem(position)
            holder.textView.text = item.nombre
            holder.textView.isSelected = true
            holder.textFecha.text = item.fecha
            holder.textTotal.text = "\$${item.total}"
            val drawable = drawables[position % drawables.size]
            holder.imageView.setImageDrawable(
                ResourcesCompat.getDrawable(holder.imageView.resources, drawable, null)
            )
        }
    }

    class TransformViewHolder(binding: ItemListaCompraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val imageView: ImageView = binding.imageItem
        val textView: TextView = binding.textNombre
        val textFecha: TextView = binding.textFecha
        val textTotal: TextView = binding.textTotal
    }

    private fun listOfItemsFrom(listas: List<com.example.shoptimize.data.ListaDeCompra>): List<ListaItem> {
        return listas.mapIndexed { index, l ->
            ListaItem(id = index.toLong(), nombre = l.nombre, total = l.calculateTotal(), fecha = l.fecha)
        }
    }

    private fun showCreateDialog(onCreate: (String) -> Unit) {
        val edit = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Nueva lista")
            .setView(edit)
            .setPositiveButton("Crear") { _, _ ->
                val name = edit.text.toString().ifBlank { "Nueva lista" }
                onCreate(name)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    data class ListaItem(val id: Long, val nombre: String, val total: Int, val fecha: String)
}