package de.oligorges.ws2812editor

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.snackbar.Snackbar
import de.oligorges.ws2812editor.BusinessObjects.Color
import java.util.ArrayList
import androidx.annotation.Nullable
import androidx.core.view.isVisible
import de.oligorges.ws2812editor.Room.device


class DeviceConfig(activity: Activity, context: Context, view: View) {

    var activity: Activity
    var context: Context
    var view: View

    private var pairedDevices: Set<BluetoothDevice>? = null
    
    init{
        this.activity = activity
        this.context = context
        this.view = view
        toogleBluetooth()
        //setupColors()
    }

    fun toogleBluetooth(){
        val wifi: TableRow =  this.activity.findViewById(R.id.deviceWifi)
        val wifiPort: TableRow =  this.activity.findViewById(R.id.deviceWifiPort)
        val bluetooth: TableRow =  this.activity.findViewById(R.id.deviceBluetooth)
        val switch : SwitchCompat = this.activity.findViewById(R.id.deviceConnection)

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                // Bluetooth
                val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                }
                if (bluetoothAdapter?.isEnabled == false) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    Snackbar.make(this.view, "Please turn on Bluetooth", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }

                var spinner = this.activity.findViewById<Spinner>(R.id.btDeviceSpinner)
                val btDeviceArray = ArrayList<String>()
                // Get BT Devices
                pairedDevices =  bluetoothAdapter?.bondedDevices?.toSet()
                pairedDevices?.forEach { device ->
                    btDeviceArray.add(device.name)
                }
                val spinnerArrayAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, btDeviceArray)
                spinner.setAdapter(spinnerArrayAdapter)



                wifi.isVisible = false
                wifiPort.isVisible = false
                bluetooth.isVisible = true
            }else{
                // Wifi

                wifi.isVisible = true
                wifiPort.isVisible = true
                bluetooth.isVisible = false
            }

        }
    }

    fun getBTDevices(name: String): String {
        pairedDevices?.forEach { device ->
            if(name == device.name){
                return device.address
            }
        }
        return ""
    }

    fun getDeviceIndex(address: String): Int
    {
        pairedDevices?.forEachIndexed { i, device ->
            if(address == device.address){
                return i
            }
        }
        return 0
    }
    fun getBTDeviceName(address: String): String {
        pairedDevices?.forEach { device ->
            if(address == device.address){
                return device.name
            }
        }
        return ""
    }

    fun setupColors(){
        var spinner = this.activity.findViewById<Spinner>(R.id.colorSpinner)
        val colorArray = ArrayList<Color>()
        colorArray.add(Color("Red", "#FF0000"))
        colorArray.add(Color("Blue", "#00FF00"))
        colorArray.add(Color("Green", "#0000FF"))
        val spinnerArrayAdapter = colorAdapter(this.context, R.layout.color_item, colorArray)
        spinner.setAdapter(spinnerArrayAdapter)
    }

    class colorAdapter(context: Context, layout: Int, data: ArrayList<Color>): ArrayAdapter<Color>(context, layout, data){

        var layout: Int
        var data: ArrayList<Color>

        init {
            this.layout = layout
            this.data = data
        }


        override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
            var listItem = convertView
            if (listItem == null)
                listItem = LayoutInflater.from(context).inflate(layout, parent, false)

            val color = data.get(position)
            val name = listItem!!.findViewById(R.id.colorName) as TextView
            name.setBackgroundColor(color.toAndroidColor())
            name.text = color.name

            return listItem
        }


    }

}