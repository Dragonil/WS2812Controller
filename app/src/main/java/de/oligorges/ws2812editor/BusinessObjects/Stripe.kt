package de.oligorges.ws2812editor.BusinessObjects

import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.R.attr.data
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStream





class Stripe (id: Int, name: String){
    var id: Int = -1
    var name: String = ""
    var leds: Vector<Int> = Vector()
    var animationID: Int = -1
    var delay: Int = -1
    var device: Device = Device(0, "", "", "", 0, 0, false)

    init{
        this.id = id
        this.name = name
    }

    constructor(id: Int, name: String, leds: Vector<Int>) : this(id, name){
        this.leds = leds
    }

    constructor(id: Int, name: String, leds: Vector<Int>, device: Device): this(id, name){
        this.leds = leds
        this.device = device
    }

    fun createJson(): JSONObject {
        var json = JSONObject()

        var device = JSONObject()
        var led = JSONArray()
        var animation = JSONObject()

        device.put("ID", this.device.id)
        device.put("Name", this.device.name)
        device.put("PIN", this.device.PWMPin)

        animation.put("ID", this.animationID)
        animation.put("delay", this.delay)

        for(l in this.leds){
            led.put(l)
        }


        json.put("device", device)
        json.put("led", led)
        json.put("animation", animation)

        return json
    }

    fun send(deviceInfo: Device): Boolean{
        var jsonstring = createJson().toString()
        if(this.device.connectionTypeWifi){
            val out: OutputStream
            try {
                val url = URL(this.device.ip+""+this.device.port)
                val urlConnection = url.openConnection() as HttpURLConnection
                out = BufferedOutputStream(urlConnection.outputStream)

                val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
                writer.write(data)
                writer.flush()
                writer.close()
                out.close()

                urlConnection.connect()
            } catch (e: Exception) {
                println(e.message)
            }


        }else{

        }
        return true
    }
}