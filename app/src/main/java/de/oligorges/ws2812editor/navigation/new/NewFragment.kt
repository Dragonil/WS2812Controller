package de.oligorges.ws2812editor.navigation.new

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import de.oligorges.ws2812editor.DeviceConfig
import de.oligorges.ws2812editor.R
import de.oligorges.ws2812editor.Room.device
import de.oligorges.ws2812editor.Room.led
import de.oligorges.ws2812editor.Room.stripe
import de.oligorges.ws2812editor.navigation.home.HomeViewModel

class NewFragment : Fragment() {

    private lateinit var newViewModel: NewViewModel
    private lateinit var device: device
    private lateinit var stripe: stripe
    private lateinit var led: ArrayList<led>
    private lateinit var config: DeviceConfig
    private var color: Map<String, Int> = mapOf("Red" to Color.RED,
                                                "Blue" to Color.BLUE,
                                                "Green" to Color.GREEN,
                                                "Magenta" to Color.MAGENTA,
                                                "Yellow" to Color.YELLOW,
                                                "Cyan" to Color.CYAN,
                                                "Orange" to Color.parseColor("#FFA500"),
                                                "Purple" to Color.parseColor("#800080")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_new, container, false)

        newViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(
                NewViewModel::class.java)



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = DeviceConfig(requireActivity(), requireContext(), this.view!!)

        val create: Button = requireActivity().findViewById(R.id.deviceCreate)
        create.setOnClickListener{
            if(validateInput()){
                newViewModel.createAll(this.device, this.stripe, *this.led.toTypedArray())

                var bundle = bundleOf("new" to true)
                this.findNavController().navigate(R.id.nav_home, bundle)
            }else{
                Snackbar.make(view, "Please set Name and Nr. of Leds", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        val cancel: Button = requireActivity().findViewById(R.id.deviceCancel)
        cancel.setOnClickListener {
            this.findNavController().navigate(R.id.nav_home)
        }
    }


    fun validateInput(): Boolean{
        var act = requireActivity()

        var dName: EditText = act.findViewById(R.id.deviceName)
        if(dName.text.toString() == "") return false

        var Location: EditText = act.findViewById(R.id.deviceLocation)
        var cType: SwitchCompat = act.findViewById(R.id.deviceConnection)



        var tar = ""
        if (cType.isChecked){
            // Bluetooth
            var target: Spinner = act.findViewById(R.id.btDeviceSpinner)
            tar = config.getBTDevices(target.selectedItem.toString())

        }else{
            // Wifi
            var target1: EditText = act.findViewById(R.id.deviceIP1)
            var target2: EditText = act.findViewById(R.id.deviceIP2)
            var target3: EditText = act.findViewById(R.id.deviceIP3)
            var target4: EditText = act.findViewById(R.id.deviceIP4)
            tar = target1.text.toString() + "." + target2.text.toString() + "." + target3.text.toString() + "." + target4.text.toString()

        }
        Log.e("Target", tar)
        var port: Int
        try {
            port = Integer.parseInt(act.findViewById<EditText>(R.id.devicePort).text.toString())
        } catch (e: NumberFormatException){
            port = 8080
        }
        var pin: Int
        try {
            pin = Integer.parseInt(act.findViewById<EditText>(R.id.devicePWM).text.toString())
        }catch (e: NumberFormatException){
            pin = 8
        }

        device = device(0, dName.text.toString(), tar, port, pin, cType.isChecked)

        var colorSpinner: Spinner = act.findViewById(R.id.colorSpinner)

        stripe = stripe(0, dName.text.toString(), Location.text.toString(), color.getOrDefault(colorSpinner.selectedItem.toString(), Color.WHITE), 0, 0, 0)


        var leds: Int
        try {
            leds = Integer.parseInt(act.findViewById<EditText>(R.id.numOfLeds).text.toString())
        }catch (e: NumberFormatException){
            return false
        }
        if(leds < 1) return false
        led = ArrayList<led>()
        for(l in 0..leds-1){
            led.add(led(0, Color.parseColor("#8A8A8A"), false, 0 ))
        }
        return true
    }
}