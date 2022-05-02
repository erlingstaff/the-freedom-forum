package com.example.thefreedomforum

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.adapters.ItemAdapter
import java.util.*


class MainActivity : AppCompatActivity() {

    private var itms = ArrayList<ArrayList<String>>()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private val PERMISSION_REQUEST_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()                          //hide the title bar
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener(DialogInterface.OnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        })
                        builder.show()
                    } else if (1 == 2) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener(DialogInterface.OnDismissListener { })
                        builder.show()
                    }
                }
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener(DialogInterface.OnDismissListener { })
                    builder.show()
                }
            }
        }

        val uuid = UUID.fromString("BEB5483E-36E1-4688-B7F5-EA07361B26A8")
        val devicename = "freedomforum"
        val bluetoothManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val newuuid = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")

        val bleAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser()






        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build()


        val devices = mutableListOf<BluetoothDevice>()
        val bluetoothGattService = bluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val device = result.device
                val deviceName = device.name
                if (deviceName == "freedomforum"){
                    device.connectGatt(this@MainActivity, false, object : BluetoothGattCallback() {
                        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                            super.onConnectionStateChange(gatt, status, newState)
                            if (newState == BluetoothProfile.STATE_CONNECTED){
                                gatt?.discoverServices()
                            }
                        }

                        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                            super.onServicesDiscovered(gatt, status)
                            if (status == BluetoothGatt.GATT_SUCCESS){
                                val srv = gatt?.services
                                for (service in srv!!){
                                    val characteristics = service.characteristics
                                    for (characteristic in characteristics){
                                        if (characteristic.uuid == uuid){
                                            Log.d("herher", "Found characteristic "+characteristic.uuid)
                                            characteristic.setValue("hei hei");
                                            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                            gatt.writeCharacteristic(characteristic);
                                            gatt.readCharacteristic(characteristic);
                                            gatt.disconnect()
                                        }
                                    }
                                }

                            }
                        }

                        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                            super.onCharacteristicRead(gatt, characteristic, status)
                            val value = characteristic!!.value
                            Log.d("herher", "Read value "+value.toString())

                        }
                    })
                }
                val deviceAddress = device.address
                val deviceRssi = result.rssi
                val deviceUUID = result.scanRecord?.serviceUuids
                val deviceData = ArrayList<String>()
                deviceData.add(deviceName)
                deviceData.add(deviceAddress)
                deviceData.add(deviceRssi.toString())
                deviceData.add(deviceUUID.toString())
                Log.d("Devices", deviceData.toString())
                devices.add(device)
                if (devices.size > 5){
                    Log.d("innom", "innom")
                    bluetoothAdapter.bluetoothLeScanner.stopScan(object : ScanCallback(){
                        override fun onScanFailed(errorCode: Int) {
                            super.onScanFailed(errorCode)
                            Log.d("Devices", "Scan failed")
                        }

                        override fun onScanResult(callbackType: Int, result: ScanResult?) {
                            super.onScanResult(callbackType, result)
                            Log.d("Devices", "Scan result")
                        }
                    })
                    devices.clear()
                }
            }
        })

        bluetoothAdapter.bluetoothLeAdvertiser

        val settingsbtn = findViewById<ImageView>(R.id.settingsbtn)
        settingsbtn.setOnClickListener{
            //TODO: open settings activity, settings for chaninging name and channel
            //val intent = Intent(this, SettingsActivity::class.java)
            //startActivity(intent)
        }
        val infobtn = findViewById<ImageView>(R.id.infobtn)
        infobtn.setOnClickListener{
            //TODO: open info activity, show FAQ, simple explenation, and explain protocol
            //val intent = Intent(this, InfoActivity::class.java)
            //startActivity(intent)
        }

        val btn = findViewById<ImageView>(R.id.button)
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