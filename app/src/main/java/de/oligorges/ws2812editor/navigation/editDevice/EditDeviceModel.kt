package de.oligorges.ws2812editor.navigation.editDevice


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import de.oligorges.ws2812editor.Room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditDeviceModel(app: Application): AndroidViewModel(app) {
    private var app: Application
    private val striperepository: StripeRepository
    private val devicerepository: DeviceRepository
    var stripe: MutableLiveData<stripe> =  MutableLiveData()
    var device:  MutableLiveData<device> = MutableLiveData()

    init {
        this.app = app
        val deviceDao = AppDatabase.getInstance(app).deviceDao()
        val stripeDao = AppDatabase.getInstance(app).stripeDao()
        devicerepository = DeviceRepository(deviceDao)
        striperepository = StripeRepository(stripeDao)

    }

    fun loadData(deviceID: Long, stripeID: Int)= viewModelScope.launch(Dispatchers.IO) {
        Log.e("Load Data", "Load " + stripeID )
        stripe.postValue(striperepository.getByID(stripeID))
        device.postValue(devicerepository.getByID(deviceID))
    }

    fun update(device: device, stripe: stripe)= viewModelScope.launch(Dispatchers.IO){
        Log.e("Update", "Device "+ device.uid +" Stripe "+stripe.uid)
        devicerepository.update(device)
        striperepository.update(stripe)
    }

    fun delete(device: device)= viewModelScope.launch(Dispatchers.IO){
        devicerepository.delete(device)
    }
}