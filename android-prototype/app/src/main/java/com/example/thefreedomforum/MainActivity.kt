package com.example.thefreedomforum

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefreedomforum.adapters.ItemAdapter
import kotlinx.coroutines.delay
import java.util.*


class MainActivity : AppCompatActivity() {

    private var itms = ArrayList<ArrayList<String>>()
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var serverLate : BluetoothGattServer
    private val PERMISSION_REQUEST_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2

    private var send = ""
    private var sent = 1
    private var sentTo = mutableListOf<String>()
    private var whiteListDevices = mutableListOf<String>()

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

        val serviceUUID = UUID.fromString("4FAFC201-1FB5-459E-8FCC-C5C9C331914B")
        val characteristicUUID = UUID.fromString("BEB5483E-36E1-4688-B7F5-EA07361B26A8")
        val startUUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")
        val bleAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser()
        val gatt = object : BluetoothGattServerCallback(){
            override fun onCharacteristicReadRequest (device : BluetoothDevice, requestId : Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
                Log.d("onCharacteristicReadRequest", "device: $device, requestId: $requestId, offset: $offset, characteristic: $characteristic")
            }
            override fun onCharacteristicWriteRequest (device : BluetoothDevice, requestId : Int, characteristic : BluetoothGattCharacteristic, preparedWrite : Boolean, responseNeeded : Boolean, offset : Int, value : ByteArray) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
                val length: Int = value.size
                val reversed = ByteArray(length)
                for (i in 0 until length) {
                    reversed[i] = value[length - (i + 1)]
                }
                val string = String(reversed)
                send = string
                sent = 0
                sentTo.clear()
                val nytest = ArrayList<String>()
                nytest.add(string)
                nytest.add(string)
                nytest.add(getTime())
                itms.add(nytest)
                adapter.notifyDataSetChanged()
                recyclerView.post { adapter.notifyDataSetChanged() }
                recyclerView.smoothScrollToPosition(itms.size-1)
                Log.d("HERHERS", "SENDTE "+string)
                serverLate.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
                //sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, 0, null)
                Log.d("onCharacteristicWriteRequest", "device: $device, requestId: $requestId, characteristic: $characteristic, preparedWrite: $preparedWrite, responseNeeded: $responseNeeded, offset: $offset, value: $value")
            }

            override fun onDescriptorReadRequest (device : BluetoothDevice, requestId : Int, offset : Int, descriptor : BluetoothGattDescriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor)
                Log.d("onDescriptorReadRequest", "device: $device, requestId: $requestId, offset: $offset, descriptor: $descriptor")
            }
            override fun onDescriptorWriteRequest (device : BluetoothDevice, requestId : Int, descriptor : BluetoothGattDescriptor, preparedWrite : Boolean, responseNeeded : Boolean, offset : Int, value: ByteArray) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value)
                Log.d("onDescriptorWriteRequest", "device: $device, requestId: $requestId, descriptor: $descriptor, preparedWrite: $preparedWrite, responseNeeded: $responseNeeded, offset: $offset, value: $value")
            }
            override fun onExecuteWrite (device : BluetoothDevice, requestId : Int, execute : Boolean){
                super.onExecuteWrite(device, requestId, execute)
                Log.d("onExecuteWrite", "device: $device, requestId: $requestId, execute: $execute")
            }
            override fun onNotificationSent (device : BluetoothDevice, requestId : Int){
                super.onNotificationSent(device, requestId)
                Log.d("onNotificationSent", "device: $device, requestId: $requestId")
            }
            override fun onMtuChanged (device : BluetoothDevice, mtu : Int){
                super.onMtuChanged(device, mtu)
                Log.d("onMtuChanged", "device: $device, mtu: $mtu")
            }
            override fun onConnectionStateChange (device : BluetoothDevice, status : Int, newState : Int){
                super.onConnectionStateChange(device, status, newState)
                if (whiteListDevices.contains(device.address) == false){
                    whiteListDevices.add(device.address)
                }
                Log.d("onConnectionStateChanged", "device: $device, status: $status, newState: $newState")
            }
            override fun onServiceAdded (status : Int, service : BluetoothGattService){
                super.onServiceAdded(status, service)
                Log.d("onServiceAdded", "status: $status, service: $service")
            }
        }


        serverLate = bluetoothManager.openGattServer(this, gatt)
        val service = BluetoothGattService(serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val writeCharacteristic = BluetoothGattCharacteristic(characteristicUUID, BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE)

        service.addCharacteristic(writeCharacteristic)
        serverLate.addService(service)

        val set = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            //.addServiceUuid(ParcelUuid(serviceUUID))
            .addServiceUuid(ParcelUuid(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")))
            .build()

        val scanResponse = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(serviceUUID))
            .build()

        bleAdvertiser.startAdvertising(set, data, scanResponse, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
                Log.d("onStartSuccess", "settingsInEffect: $settingsInEffect")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.d("onStartFailure", "errorCode: $errorCode")
            }
        })





        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build()

        var connected = 0
        val devices = mutableListOf<BluetoothDevice>()
        val bluetoothGattService = bluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val deviceUUID = result.scanRecord?.serviceUuids
                val device = result.device
                val deviceName = device.name
                val deviceAddress = device.uuids
                Log.d("UUIDSS", deviceUUID.toString())
                if (deviceUUID != null) {
                    device.connectGatt(this@MainActivity, false, object : BluetoothGattCallback() {
                        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                            super.onConnectionStateChange(gatt, status, newState)
                            if (newState == BluetoothProfile.STATE_CONNECTED){
                                connected += 1
                                Log.d("connectedd", connected.toString())
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
                                            Log.d("utenfor", "utenfor")
                                            if (sentTo.find { it.contains(device.address) } == null){
                                                Log.d("herher", "Found characteristic "+characteristic.uuid)
                                                if (send != ""){
                                                    Log.d("Burdes", send)
                                                    characteristic.setValue(send);
                                                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                                    gatt.writeCharacteristic(characteristic);
                                                    sentTo.add(device.address)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (whiteListDevices.contains(device.address)){
                                Log.d("whiteListDevices", whiteListDevices.toString() + " vs " + device.address)
                                removeDevice(gatt)
                                //gatt?.disconnect()
                                //gatt?.close()
                            }
                            else{
                                connected -= 1
                                Log.d("connectedd", connected.toString())
                                gatt?.disconnect()
                                gatt?.close()
                            }
                        }

                        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                            super.onCharacteristicRead(gatt, characteristic, status)
                            val value = characteristic!!.value
                            Log.d("herher", "Read value "+value.toString())

                        }

                        fun removeDevice(gatt : BluetoothGatt?){
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    connected -= 1
                                    Log.d("connectedd", connected.toString())
                                    gatt?.disconnect()
                                    gatt?.close()
                                },
                                10000 // value in milliseconds
                            )
                        }

                    })
                val deviceAddress = device.address
                val deviceRssi = result.rssi
                val deviceData = ArrayList<String>()
                deviceData.add(deviceName)
                deviceData.add(deviceAddress)
                deviceData.add(deviceRssi.toString())
                deviceData.add(deviceUUID.toString())
                Log.d("Devices", deviceData.toString())
            }
        }
        })

        bluetoothAdapter.bluetoothLeAdvertiser

        val settingsbtn = findViewById<ImageView>(R.id.settingsbtn)
        settingsbtn.setOnClickListener{
            //TODO: open settings activity, settings for chaninging name and channel
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        val infobtn = findViewById<ImageView>(R.id.infobtn)
        infobtn.setOnClickListener{
            //TODO: open info activity, show FAQ, simple explenation, and explain protocol
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)

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
        recyclerView.smoothScrollToPosition(itms.size-1)

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
        send = message
        sentTo.clear()

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