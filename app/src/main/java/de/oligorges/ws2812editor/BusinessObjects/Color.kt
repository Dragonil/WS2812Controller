package de.oligorges.ws2812editor.BusinessObjects


import android.graphics.Color

class Color(name: String = "", hex: String){

    var name: String
    var hex: String

    init{
        this.name = name
        this.hex = hex
    }

    fun toAndroidColor(): Int{
        return Color.parseColor(this.hex)
    }


}