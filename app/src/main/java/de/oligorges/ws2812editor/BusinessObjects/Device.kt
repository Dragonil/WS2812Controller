package de.oligorges.ws2812editor.BusinessObjects

class Device (id: Int, name: String, location: String = "", ip: String, port: Int, pwmPin: Int, connectionType: Boolean){

    var id = 0
    var name = ""
    var location = ""
    var ip = ""
    var port = 80
    var PWMPin = 8
    var connectionTypeWifi = true

    init {
        this.id = id
        this.name = name
        this.location = location
        this.ip = ip
        this.port = port
        this.PWMPin = pwmPin
        this.connectionTypeWifi = connectionType
    }

}