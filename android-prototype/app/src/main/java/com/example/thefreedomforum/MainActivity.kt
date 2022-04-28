package com.example.thefreedomforum

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.adapters.ItemAdapter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()                          //hide the title bar
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val message = findViewById<EditText>(R.id.EditText).text.toString()
            sanitizeAndBroadcast(message)
        }

        val recv = findViewById<RecyclerView>(R.id.recv)
        recv.layoutManager = LinearLayoutManager(this)

        val itemAdapter = ItemAdapter(this, getItemList())
        recv.adapter = itemAdapter

    }

    private fun getItemList(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0..15){
            list.add("Item $i")
        }
        return list
    }

    private fun sanitizeAndBroadcast(message: String) {
        val spaceGuard = message.replace(" ", "")
        if (spaceGuard.isEmpty()) {
            Log.d("isEmpty", "isEmpty")
            val toast = Toast.makeText(this, "Empty\nMessage\n", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
        val charAmount = message.length
        if (charAmount > 160){
            Log.d("tooLong", "tooLong")
            val toast = Toast.makeText(this, "Message\nToo Long\n", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }


    }

}