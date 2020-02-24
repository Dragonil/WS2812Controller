package de.oligorges.ws2812editor.navigation.editDevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import de.oligorges.ws2812editor.DeviceConfig
import de.oligorges.ws2812editor.R
import de.oligorges.ws2812editor.Room.device
import de.oligorges.ws2812editor.Room.stripe
import java.util.ArrayList
import android.widget.Toast
import de.oligorges.ws2812editor.MainActivity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import de.oligorges.ws2812editor.Room.led


class EditDeviceFragment : Fragment() {

    private lateinit var editDeviceModel: EditDeviceModel
    private lateinit var device: device
    private lateinit var stripe: stripe
    private lateinit var config: DeviceConfig
    private var color: Map<Int, Int> = mapOf(Color.GREEN to 0,Color.RED to 1,
                                                Color.BLUE to 2,
                                                Color.YELLOW to 3,
                                                Color.parseColor("#FFA500") to 4,
                                                Color.CYAN to 5,
                                                Color.MAGENTA to 6,
                                                Color.parseColor("#800080") to 7)
    private var newColor: Map<String, Int> = mapOf( "Red" to Color.RED,
                                                    "Blue" to Color.BLUE,
                                                    "Green" to Color.GREEN,
                                                    "Magenta" to Color.MAGENTA,
                                                    "Yellow" to Color.YELLOW,
                                                    "Cyan" to Color.CYAN,
                                                    "Orange" to Color.parseColor("#FFA500"),
                                                    "Purple" to Color.parseColor("#800080")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editDeviceModel =
            ViewModelProvider(this).get(EditDeviceModel::class.java)
        val root = inflater.inflate(R.layout.fragment_edit_device, container, false)


        var stripeId:Int? = arguments?.getInt("stripeID")
        if(stripeId == null) stripeId = 0

        var deviceId:Long? = arguments?.getLong("deviceID")
        if(deviceId == null) deviceId = 0L

        Log.e("ARG", "Device "+ deviceId + " Stripe "+ stripeId)

        editDeviceModel.loadData(deviceId, stripeId)//stripes.get(id)!!

        editDeviceModel.stripe.observe(this.viewLifecycleOwner, Observer {
            setStripeInfo(it)
        })

        editDeviceModel.device.observe(this.viewLifecycleOwner, Observer {
            setDeviceinfo(it)
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = DeviceConfig(requireActivity(), requireContext(), this.view!!)
        initNavButtons()
    }


    fun setDeviceinfo(d: device){
        var act = requireActivity()
        this.device = d


        var cType: SwitchCompat = act.findViewById(R.id.deviceConnection)
        cType.isChecked = device.connectionTypeBluetooth
        if(device.connectionTypeBluetooth) {
            var target: Spinner = act.findViewById(R.id.btDeviceSpinner)
            target.setSelection(config.getDeviceIndex(device.ip))
        }else{
            var target = device.ip.split('.')
            Log.e("IP", device.ip)
            var target1: EditText = act.findViewById(R.id.deviceIP1)
            target1.setText(target[0])
            var target2: EditText = act.findViewById(R.id.deviceIP2)
            target2.setText(target[1])
            var target3: EditText = act.findViewById(R.id.deviceIP3)
            target3.setText(target[2])
            var target4: EditText = act.findViewById(R.id.deviceIP4)
            target4.setText(target[3])

            var port: EditText = act.findViewById(R.id.devicePort)
            port.setText(device.port.toString())
        }

        var pin: EditText = act.findViewById(R.id.devicePWM)
        pin.setText(device.PWMPin.toString())

        var delete: Button = act.findViewById(R.id.deviceDelete)
        delete.setOnClickListener {
            AlertDialog.Builder(act)
                .setTitle("Delete "+ device.name)
                .setMessage("Do you really want to delete the Device?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        editDeviceModel.delete(device)
                        this.findNavController().navigate(R.id.nav_home)
                    })
                .setNegativeButton("No", null).show()
        }

    }

    fun validateInput(): Boolean{
        var act = requireActivity()

        var dName: EditText = act.findViewById(R.id.deviceName)
        if(dName.text.toString() == "") return false
        device.name = dName.text.toString()
        stripe.name = dName.text.toString()


        var cType: SwitchCompat = act.findViewById(R.id.deviceConnection)

        device.connectionTypeBluetooth = cType.isChecked

        if (cType.isChecked){
            // Bluetooth
            var target: Spinner = act.findViewById(R.id.btDeviceSpinner)
            device.ip = config.getBTDevices(target.selectedItem.toString())

        }else{
            // Wifi
            var target1: EditText = act.findViewById(R.id.deviceIP1)
            var target2: EditText = act.findViewById(R.id.deviceIP2)
            var target3: EditText = act.findViewById(R.id.deviceIP3)
            var target4: EditText = act.findViewById(R.id.deviceIP4)
            device.ip = target1.text.toString() + "." + target2.text.toString() + "." + target3.text.toString() + "." + target4.text.toString()

            try{
                device.port = Integer.parseInt(act.findViewById<EditText>(R.id.devicePort).text.toString())
            } catch (e: NumberFormatException){

            }
        }
        try{
            device.PWMPin = Integer.parseInt(act.findViewById<EditText>(R.id.devicePWM).text.toString())
        } catch (e: NumberFormatException){

        }

        var colorSpinner: Spinner = act.findViewById(R.id.colorSpinner)
        stripe.color = newColor.getOrDefault(colorSpinner.selectedItem.toString(), Color.WHITE)

        var Location: EditText = act.findViewById(R.id.deviceLocation)

        stripe.location = Location.text.toString()

        try{
            var leds: EditText = act.findViewById(R.id.numOfLeds)
            var num = Integer.parseInt(leds.text.toString())

        } catch (e: NumberFormatException){

        }



        return true
    }



    fun setStripeInfo(s: stripe){
        var act = requireActivity()
        this.stripe = s

        var dName: EditText = act.findViewById(R.id.deviceName)
        dName.setText(stripe.name)

        var Location: EditText = act.findViewById(R.id.deviceLocation)
        Location.setText(stripe.location)

        var colorSpinner: Spinner = act.findViewById(R.id.colorSpinner)
        colorSpinner.setSelection(color.getOrDefault(stripe.color, 0))
    }

    fun initNavButtons(){
        var act = requireActivity()
        var save: Button = act.findViewById(R.id.deviceSave)
        save.setOnClickListener {

            if(validateInput()){
                editDeviceModel.update(device, stripe)
                var bundle = bundleOf("stripeID" to stripe.uid)
                this.findNavController().navigate(R.id.nav_edit, bundle)
            }

        }

        var cancel: Button = act.findViewById(R.id.deviceCancel)
        cancel.setOnClickListener {
            var bundle = bundleOf("stripeID" to stripe.uid)
            this.findNavController().navigate(R.id.nav_edit, bundle)
        }
    }

}