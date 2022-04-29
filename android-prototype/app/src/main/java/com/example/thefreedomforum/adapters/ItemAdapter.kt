package com.example.thefreedomforum.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(val context: Context, val itemList: ArrayList<ArrayList<String>>)
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
        val name = item.get(0)
        val message = item.get(1)
        val time = item.get(2)

        if (name == "Me") {
            val constraintParams = holder.recMessage.layoutParams as ConstraintLayout.LayoutParams
            constraintParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            constraintParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            holder.recMessage.setBackgroundResource(R.drawable.rounded_recycler_view_item_blue)
        }

        holder.recName.text = name
        holder.recMessage.text = message
        holder.recTime.text = time
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
