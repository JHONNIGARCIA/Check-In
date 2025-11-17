package com.example.sh

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sh.databinding.ItemCheckinBinding

class CheckinAdapter(private val items: List<String>) : RecyclerView.Adapter<CheckinAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCheckinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parts = items[position].split(",")
        if (parts.size == 2) {
            holder.binding.timestamp.text = parts[0]
            holder.binding.source.text = "Origen: ${parts[1]}"
        } else {
            holder.binding.timestamp.text = items[position]
            holder.binding.source.text = "Origen: Desconocido"
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(val binding: ItemCheckinBinding) : RecyclerView.ViewHolder(binding.root)
}
