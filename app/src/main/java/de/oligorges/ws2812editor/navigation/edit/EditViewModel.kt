package de.oligorges.ws2812editor.navigation.edit

import android.app.Application
import android.app.DownloadManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.graphics.toColor
import androidx.lifecycle.*
import de.oligorges.ws2812editor.Room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class EditViewModel(app: Application): AndroidViewModel(app) {
    private var app: Application
    private val striperepository: StripeRepository
    private val ledrepository: LedRepository
    private val devicerepository: DeviceRepository
    var stripe: MutableLiveData<stripe> =  MutableLiveData()
    var leds:  MutableLiveData<List<led>> = MutableLiveData()

    init {
        this.app = app
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val ledDao = AppDatabase.getInstance(app).ledDao()
        val stripeDao = AppDatabase.getInstance(app).stripeDao()
        val deviceDao = AppDatabase.getInstance(app).deviceDao()
        ledrepository = LedRepository(ledDao)
        striperepository = StripeRepository(stripeDao)
        devicerepository= DeviceRepository(deviceDao)

    }

    fun loadData(stripeID: Int)= viewModelScope.launch(Dispatchers.IO) {
        Log.e("Load Data", "Load " + stripeID )
        leds.postValue(ledrepository.getByStripeID(stripeID))
        stripe.postValue(striperepository.getByID(stripeID))
    }

    fun updateLeds(vararg leds: led) = viewModelScope.launch(Dispatchers.IO){
        ledrepository.updateAll(*leds)
    }

    fun sendData(stripeID: Int) = viewModelScope.launch(Dispatchers.IO){
        val stripe = striperepository.getByID(stripeID)
        val device = devicerepository.getByID(stripe.deviceID)
        val leds = ledrepository.getByStripeID(stripeID)
        var json: JSONObject = createJson(stripe, device, leds)
        Log.e("Send", json.toString())
        if(device.connectionTypeBluetooth){
            sendBluetooth(device, json)
        }else{
            sendWifi(device, json)
        }

    }

    fun sendBluetooth(device: device, json: JSONObject){
        Log.e("Send", "Bluetooth")
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("Test", UUID.fromString(device.ip))
        }
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                val mmOutStream: OutputStream = it.outputStream
                try {
                    mmOutStream.write(json.toString().toByteArray())
                } catch (e: IOException) {
                    Log.e(TAG, "Error occurred when sending data", e)
                    return
                }
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }



    fun sendWifi(device: device, json: JSONObject){
        Log.e("Send", "Wifi")

        val queue = Volley.newRequestQueue(app)
        val url: String = "http://"+device.ip+":"+device.port+"/update"
        var jsonRequest = JsonObjectRequest(Request.Method.POST, url , json, Response.Listener { response ->
                Log.e("Http", "DataSend")
            },
            Response.ErrorListener { error ->
                Log.e("Http", error.toString())
            })
        queue.add(jsonRequest)
    }

    fun createJson(s: stripe, d: device, ledList: List<led>): JSONObject {
        var json = JSONObject()

        var device = JSONObject()
        var leds = JSONArray()
        var animation = JSONObject()

        device.put("ID", d.uid)
        device.put("Name", d.name)
        device.put("PIN", d.PWMPin)

        animation.put("ID", s.animationID)
        animation.put("delay", s.delay)

        for(l in ledList){
            var led = JSONObject()
            led.put("State", l.state)
            var color = JSONArray()
            led.put("Color", color)
            color.put(Color.pack(l.color).toColor().red()*255)
            color.put(Color.pack(l.color).toColor().green()*255)
            color.put(Color.pack(l.color).toColor().blue()*255)
            leds.put(led)
        }


        json.put("device", device)
        json.put("leds", leds)
        json.put("animation", animation)

        return json
    }


    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(stripe: stripe) = viewModelScope.launch(Dispatchers.IO) {
        striperepository.insert(stripe)
    }
}