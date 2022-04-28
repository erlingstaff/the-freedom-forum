package com.example.thefreedomforum.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
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

        holder.recName.text = name
        holder.recMessage.text = message

        var tim = ""
        var min = ""

        val rightNow = Calendar.getInstance()
        val currentHourIn24Format: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        if (currentHourIn24Format == 0) {
            tim = "00"
        } else {
            tim = currentHourIn24Format.toString()
        }

        val currentMinute: Int = rightNow.get(Calendar.MINUTE)
        if (currentMinute < 10) {
            min = "0$currentMinute"
        } else {
            min = currentMinute.toString()
        }
        val time = "$tim:$min"

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
