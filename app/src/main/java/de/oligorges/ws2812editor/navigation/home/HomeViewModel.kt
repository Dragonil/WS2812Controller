package de.oligorges.ws2812editor.navigation.home

import android.app.Application
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.oligorges.ws2812editor.Room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(app: Application): AndroidViewModel(app) {

    private var app: Application
    private val sRepository: StripeRepository
    private val dRepository: DeviceRepository
    val allStripes: LiveData<List<stripe>>

    init {
        this.app = app
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val stripeDao = AppDatabase.getInstance(app).stripeDao()
        val deviceDao = AppDatabase.getInstance(app).deviceDao()
        sRepository = StripeRepository(stripeDao)
        dRepository = DeviceRepository(deviceDao)
        allStripes = sRepository.allStripes

    }
    fun getStatus(stripe: stripe, button: ImageButton)= viewModelScope.launch(Dispatchers.IO) {
        var device = dRepository.getByID(stripe.deviceID)
        val queue = Volley.newRequestQueue(app)
        val url: String = "http://"+device.ip+":"+device.port+"/status"
        val stringRequest = StringRequest(Request.Method.GET,  url , Response.Listener { response ->
            if(response == "1"){
                button.setBackgroundColor(stripe.color)
                button.setTag("on")
            }
        },
            Response.ErrorListener { error ->
                Log.e("Http", error.toString())
            })
        queue.add(stringRequest)
    }

    fun toogleStrip(stripe: stripe)= viewModelScope.launch(Dispatchers.IO) {
        var device = dRepository.getByID(stripe.deviceID)
        val queue = Volley.newRequestQueue(app)
        val url: String = "http://"+device.ip+":"+device.port+"/toogle"
        val stringRequest = StringRequest(Request.Method.GET,  url , Response.Listener { response ->
            Log.e("HTTP", response)
        },
            Response.ErrorListener { error ->
                Log.e("HTTP", error.toString())
            })
        queue.add(stringRequest)
    }


}