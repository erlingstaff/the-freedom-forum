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

    private var itms = ArrayList<ArrayList<String>>()

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

        itms = getItemList()

        val itemAdapter = ItemAdapter(this, itms)
        recv.adapter = itemAdapter

        val nytest = ArrayList<String>()
        nytest.add("test")
        nytest.add("test2")
        itms.add(nytest)

    }

    private fun getItemList(): ArrayList<ArrayList<String>> {
        val list = ArrayList<ArrayList<String>>()
        for (i in 0..15){
            val item = ArrayList<String>()
            item.add("Navn her")
            item.add("Melding her")
            list.add(item)
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
        val new_item = ArrayList<String>()
        new_item.add("Sender")
        new_item.add(message)
        itms.add(new_item)


    }

}