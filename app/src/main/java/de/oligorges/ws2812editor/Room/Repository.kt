package de.oligorges.ws2812editor.Room

import android.util.Log
import androidx.lifecycle.LiveData

val TAG = "Repo"

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class StripeRepository(private val stripeDao: StripeDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allStripes: LiveData<List<stripe>> = stripeDao.getAll()

    suspend fun getByID(stripeID: Int): stripe{
        Log.e(TAG, "Get Stripe by" + stripeID)
        return stripeDao.getById(stripeID)
    }

    suspend fun insert(stripe: stripe): Long {
        Log.e(TAG, "Create Stripe")
        return stripeDao.insert(stripe)
    }

    suspend fun update(stripe: stripe) {
        Log.e(TAG, "Update Stripe")
        stripeDao.update(stripe)
    }
}

class LedRepository(private val ledDao: LedDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allLeds: LiveData<List<led>> = ledDao.getAll()

    suspend fun insert(led: led) {
        Log.e(TAG, "Add Led")
        ledDao.insert(led)
    }

    fun updateAll(vararg leds: led){
        Log.e(TAG, "Update "+leds.size+" Leds")
        ledDao.updateAll(*leds)
    }

    suspend fun getByStripeID(stripeID: Int): List<led>{
        Log.e(TAG, "Get Leds for "+ stripeID +" Stripe")
        return ledDao.getByStripeID(stripeID)
    }

    suspend fun insertAll(vararg leds: led) {
        Log.e(TAG, "Add "+leds.size+" Leds")
        ledDao.insertAll(*leds)
    }
}

class DeviceRepository(private val deviceDao: DeviceDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allDevices: LiveData<List<device>> = deviceDao.getAll()

    suspend fun getByID(deviceID: Long): device{
        Log.e(TAG, "Get Stripe by" + deviceID)
        return deviceDao.getById(deviceID)
    }

    suspend fun insert(device: device): Long {
        Log.e(TAG, "Add Device")
        return deviceDao.insert(device)
    }

    suspend fun update(device: device) {
        Log.e(TAG, "Update Device")
        return deviceDao.update(device)
    }

    suspend fun delete(device: device) {
        Log.e(TAG, "Update Device")
        return deviceDao.delete(device)
    }
}