package com.example.shoptimize.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoptimize.R
import com.example.shoptimize.data.relations.ListaConProductos
import com.example.shoptimize.databinding.ItemListaCompraBinding

class HistoryAdapter : ListAdapter<ListaConProductos, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    private val drawables = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
        R.drawable.avatar_6,
        R.drawable.avatar_7,
        R.drawable.avatar_8,
        R.drawable.avatar_9,
        R.drawable.avatar_10,
        R.drawable.avatar_11,
        R.drawable.avatar_12
    )
    
    private var onItemClickListener: ((Int) -> Unit)? = null
    
    fun setItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemListaCompraBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val purchase = getItem(position)
        holder.bind(purchase, drawables[position % drawables.size])
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(purchase.lista.id)
        }
    }

    class HistoryViewHolder(private val binding: ItemListaCompraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val imageView: ImageView = binding.imageItem
        private val textNombre: TextView = binding.textNombre
        private val textFecha: TextView = binding.textFecha
        private val textTotal: TextView = binding.textTotal

        fun bind(purchase: ListaConProductos, drawableRes: Int) {
            textNombre.text = purchase.lista.nombre
            textNombre.isSelected = true
            textFecha.text = purchase.lista.fecha
            textTotal.text = "$${purchase.calculateTotal()}"
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(imageView.resources, drawableRes, null)
            )
            // Ocultar botón de eliminar en historial
            binding.btnDeleteLista.visibility = View.GONE
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<ListaConProductos>() {
        override fun areItemsTheSame(oldItem: ListaConProductos, newItem: ListaConProductos): Boolean {
            return oldItem.lista.id == newItem.lista.id
        }

        override fun areContentsTheSame(oldItem: ListaConProductos, newItem: ListaConProductos): Boolean {
            return oldItem.lista == newItem.lista && oldItem.productos == newItem.productos
        }
    }
}
