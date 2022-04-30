package com.example.thefreedomforum

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.adapters.ItemAdapter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var itms = ArrayList<ArrayList<String>>()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()                          //hide the title bar
        setContentView(R.layout.activity_main)
        val bluetoothManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }




        val btn = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.EditText)
        btn.setOnClickListener {
            val message = findViewById<EditText>(R.id.EditText).text.toString()
            editText.setText("")
            sanitizeAndBroadcast(message)
        }
        editText.setOnClickListener{
            Log.d("EditText", editText.getText().toString())
            editText.setText("")
        }

        val recv = findViewById<RecyclerView>(R.id.recv)
        recv.layoutManager = LinearLayoutManager(this)
        itms = getItemList()
        val itemAdapter = ItemAdapter(this, itms)
        recv.adapter = itemAdapter

        adapter = itemAdapter
        recyclerView = recv

        val nytest = ArrayList<String>()
        nytest.add("test")
        nytest.add("test2")
        nytest.add(getTime())
        itms.add(nytest)

    }

    private fun getItemList(): ArrayList<ArrayList<String>> {
        val list = ArrayList<ArrayList<String>>()
        for (i in 0..15){
            val item = ArrayList<String>()
            item.add("Navn her")
            item.add("Melding her")
            item.add(getTime())

            list.add(item)
        }
        return list
    }

    private fun sanitizeAndBroadcast(message: String) {

        val messageField = findViewById<EditText>(R.id.EditText)
        messageField.getText().clear();


        val spaceGuard = message.replace(" ", "")
        Log.d("MainActivity", "Message: $spaceGuard")
        if (spaceGuard.isEmpty()) {
            Log.d("isEmpty", "isEmpty")
            val toast = Toast.makeText(this, "Empty\nMessage\n", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        val charAmount = message.length
        if (charAmount > 160){
            Log.d("tooLong", "tooLong")
            val toast = Toast.makeText(this, "Message\nToo Long\n", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        val new_item = ArrayList<String>()
        new_item.add("Me")
        new_item.add(message)
        new_item.add(getTime())

        itms.add(new_item)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(adapter.itemCount - 1)


    }


    private fun getTime(): String {
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
        return time
    }

}