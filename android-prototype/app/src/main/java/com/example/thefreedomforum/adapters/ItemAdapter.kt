package com.example.thefreedomforum.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.R

class ItemAdapter(val context: Context, val itemList: ArrayList<String>)
    : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_view_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList.get(position)
        holder.recName.text = "Nummer $item"
        holder.recMessage.text = "Melding herifra"
        holder.recTime.text = "20:20"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recName = view.findViewById(R.id.recyclerName) as TextView
        val recMessage = view.findViewById(R.id.recyclerMessage) as TextView
        val recTime = view.findViewById(R.id.recyclerTime) as TextView
    }
}
