package de.oligorges.ws2812editor.navigation.new

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import de.oligorges.ws2812editor.Room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewViewModel(app: Application): AndroidViewModel(app) {

    private var app: Application
    private val striperepository: StripeRepository
    private val devicerepository: DeviceRepository
    private val ledrepository: LedRepository

    init {
        this.app = app
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val stripeDao = AppDatabase.getInstance(app).stripeDao()
        val deviceDao = AppDatabase.getInstance(app).deviceDao()
        val ledDao = AppDatabase.getInstance(app).ledDao()
        striperepository = StripeRepository(stripeDao)
        devicerepository = DeviceRepository(deviceDao)
        ledrepository = LedRepository(ledDao)
    }

    fun createStripe(stripe: stripe) = viewModelScope.launch(Dispatchers.IO) {
        striperepository.insert(stripe)
    }

    fun createDevice(device: device) = viewModelScope.launch(Dispatchers.IO) {
        devicerepository.insert(device)
    }

    fun createLeds(vararg leds: led) = viewModelScope.launch(Dispatchers.IO) {
        ledrepository.insertAll(*leds)
    }

    fun createAll(device: device, stripe: stripe,  vararg leds: led) = viewModelScope.launch(Dispatchers.IO) {
        var deviceID = devicerepository.insert(device)
        Log.e("Device", "Created: "+deviceID.toString())
        stripe.deviceID = deviceID
        var stripeID = striperepository.insert(stripe)
        Log.e("Stripe", "Created: "+stripeID.toString())
        leds.forEach { led -> led.stripeID = stripeID }
        ledrepository.insertAll(*leds)
    }
}