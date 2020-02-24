package de.oligorges.ws2812editor.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface StripeDao{

    @Query("Select * from stripe")
    fun getAll(): LiveData<List<stripe>>

    @Query("SELECT * FROM stripe WHERE uid = :id LIMIT 1")
    fun getById(id: Int): stripe

    @Update
    fun update(stripe: stripe)

    @Insert
    fun insert(stripe: stripe): Long

    @Delete
    fun delete(stripe: stripe)

}

@Dao
interface LedDao{

    @Query("Select * from led")
    fun getAll(): LiveData<List<led>>

    @Query("SELECT * FROM led WHERE uid = :id LIMIT 1")
    fun getById(id: Int): led

    @Query("SELECT * FROM led WHERE StripeID = :stripeID")
    fun getByStripeID(stripeID: Int): List<led>

    @Update
    fun updateAll(vararg led: led)

    @Update
    fun update(led: led)

    @Insert
    fun insertAll(vararg leds: led)

    @Insert
    fun insert(led: led): Long

    @Delete
    fun delete(led: led)

}

@Dao
interface DeviceDao{

    @Query("Select * from device")
    fun getAll(): LiveData<List<device>>

    @Query("SELECT * FROM device WHERE uid IN (:id) LIMIT 1")
    fun getById(id: Long): device

    @Update
    fun update(device: device)

    @Insert
    fun insert(device: device): Long

    @Delete
    fun delete(device: device)

}