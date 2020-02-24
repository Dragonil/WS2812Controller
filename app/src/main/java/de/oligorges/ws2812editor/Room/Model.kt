package de.oligorges.ws2812editor.Room

import androidx.room.*
import java.util.*


@Entity(tableName = "stripe",
    indices = arrayOf(Index(value = ["DeviceID"])),
    foreignKeys = arrayOf(ForeignKey(entity = device::class,
    parentColumns = arrayOf("uid"),
    childColumns = arrayOf("DeviceID"),
    onDelete = ForeignKey.CASCADE)))
data class stripe(
    @PrimaryKey (autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "Name") var name: String,
    @ColumnInfo(name = "Location") var location: String,
    @ColumnInfo(name = "Color") var color: Int,
    @ColumnInfo(name = "Animation") var animationID: Int,
    @ColumnInfo(name = "Delay") var delay: Int,
    @ColumnInfo(name = "DeviceID") var deviceID: Long

)

@Entity(tableName = "device")
data class device(
    @PrimaryKey (autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "Name") var name: String,
    @ColumnInfo(name = "IP") var ip: String,
    @ColumnInfo(name = "Port") var port: Int,
    @ColumnInfo(name = "Pin") var PWMPin: Int,
    @ColumnInfo(name = "Type") var connectionTypeBluetooth: Boolean
)

@Entity(tableName = "led",
    indices = arrayOf(Index(value = ["StripeID"])),
    foreignKeys = arrayOf(ForeignKey(entity = stripe::class,
    parentColumns = arrayOf("uid"),
    childColumns = arrayOf("StripeID"),
    onDelete = ForeignKey.CASCADE)))

data class led(
    @PrimaryKey (autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "Color") var color: Int,
    @ColumnInfo(name = "State") var state: Boolean,
    @ColumnInfo(name = "StripeID") var stripeID: Long,
    @Ignore var changed: Boolean
){
    constructor(uid: Int, color: Int, state: Boolean, stripeID: Long): this(uid, color, state, stripeID, false)

}