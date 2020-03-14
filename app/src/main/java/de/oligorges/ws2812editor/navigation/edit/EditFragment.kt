package de.oligorges.ws2812editor.navigation.edit

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.TintableCompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import de.oligorges.ws2812editor.BusinessObjects.Stripe
import de.oligorges.ws2812editor.R
import de.oligorges.ws2812editor.Room.led
import de.oligorges.ws2812editor.Room.stripe
import de.oligorges.ws2812editor.holocolorpicker.ColorPicker
import de.oligorges.ws2812editor.holocolorpicker.SVBar
import java.lang.Exception
import java.util.*

class EditFragment : Fragment() {

    private lateinit var editViewModel: EditViewModel

    var color: Int = Color.parseColor("#0000FF")

    var leds: Vector<AppCompatRadioButton> = Vector()
    lateinit var stripes: MutableMap<Int, Stripe>
    lateinit var stripe: stripe
    lateinit var stripeLed: List<led>
    lateinit var picker: ColorPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        editViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(EditViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        setHasOptionsMenu(true);
        stripes = loadData()

        var id:Int? = arguments?.getInt("stripeID")
        if(id == null) id = 0

        editViewModel.loadData(id)//stripes.get(id)!!

        editViewModel.stripe.observe(this.viewLifecycleOwner, Observer {
            stripe = it
            Log.e("DB", "Stripe: "+ stripe.name)
            var name = requireActivity().findViewById<TextView>(R.id.StripeName)
            name.text = stripe.name
        })

        editViewModel.leds.observe(this.viewLifecycleOwner, Observer {
            Log.e("DB", "Leds: "+  it.size)
            generateLED(it)
        })

        return root
    }

    fun saveChanges(){
        var ledList = ArrayList<led>()
        for (l in stripeLed){
            if(l.changed){
                ledList.add(l)

                Log.e("State", l.state.toString()  +" ?")
            }
        }
        editViewModel.updateLeds( *ledList.toTypedArray())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Code
        selectColor()
        toogleAll()

        val animations: Button = requireActivity().findViewById(R.id.setAnimation)
        animations.setOnClickListener(){
            var bundle = bundleOf("stripeID" to stripe.uid)
            this.findNavController().navigate(R.id.nav_animation, bundle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        if (item.getItemId() == R.id.action_settings) {
            var bundle = bundleOf("stripeID" to stripe.uid, "deviceID" to stripe.deviceID)
            this.findNavController().navigate(R.id.nav_editDevice, bundle)
            return true
        } else if (item.getItemId() == R.id.action_save) {
            saveChanges()
            return true
        }
        else if (item.getItemId() == R.id.action_update) {
            saveChanges()
            editViewModel.sendData(stripe.uid)
            return true
        }
        else {
            return super.onOptionsItemSelected(item)

        }
    }

    fun toogleAll(){
        val toggleOn: Button = requireActivity().findViewById(R.id.LedsAllOn)
        toggleOn.setOnClickListener(){
            for(i in 0..this.leds.size-1){
                leds.get(i).setTag("on")
                leds.get(i).isChecked = true
                this.stripeLed.get(i).state = true
                this.stripeLed.get(i).changed = true
            }
        }

        val toggleOff: Button = requireActivity().findViewById(R.id.LedsAllOff)
        toggleOff.setOnClickListener(){
            for(i in 0..this.leds.size-1){
                leds.get(i).setTag("off")
                leds.get(i).isChecked = false
                this.stripeLed.get(i).state = false
                this.stripeLed.get(i).changed = true
            }
        }
    }

    fun generateLED(ledList: List<led>){
        stripeLed = ledList
        for (i in 0..this.stripeLed.size-1){
            //val radioButton = RadioButton(this)
            val radioButton = AppCompatRadioButton(this.context)
            val textView = TextView(this.context)
            textView.text = "" + i
            textView.gravity = Gravity.CENTER

            setAppCompatRadioButtonColor(radioButton, Color.parseColor("#8A8A8A"), this.stripeLed.get(i).color)


            radioButton.id = i
            radioButton.text = ""
            radioButton.buttonDrawable = ContextCompat.getDrawable(this.requireContext(), R.drawable.ledradio)
            radioButton.setPadding(10, 0, 10, 10);


            if(this.stripeLed.get(i).state){
                radioButton.isChecked = true
                radioButton.setTag("on")
            }else{
                radioButton.isChecked = false
                radioButton.setTag("off")
            }


            radioButton.setOnClickListener(){
                val rb = it as AppCompatRadioButton
                if(rb.getTag().toString() == "on"){
                    rb.setTag("off")
                    rb.isChecked = false
                    this.stripeLed.get(i).state = false
                    this.stripeLed.get(i).changed = true
                }
                else{
                    rb.setTag("on")
                    this.stripeLed.get(i).state = true
                    this.stripeLed.get(i).changed = true
                    this.picker.oldCenterColor = color
                    this.picker.color = this.stripeLed.get(i).color

                }
            }
            radioButton.setOnLongClickListener{
                val rb = it as AppCompatRadioButton
                rb.setTag("on")
                rb.isChecked = true
                setAppCompatRadioButtonColor(radioButton, Color.parseColor("#8A8A8A"), color)
                this.stripeLed.get(i).color = color
                this.stripeLed.get(i).changed = true
                true
            }


            val textRow = requireActivity().findViewById<TableRow>(R.id.textRow)
            textRow.addView(textView)

            val ledRow = requireActivity().findViewById<TableRow>(R.id.ledRow)
            ledRow.addView(radioButton)
            leds.addElement(radioButton)
        }

    }

    fun setAppCompatRadioButtonColor(radioButton: AppCompatRadioButton, uncheckedColor: Int, checkedColor: Int) {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_checked)  // checked
            ),
            intArrayOf(uncheckedColor, checkedColor)
        )
        (radioButton as TintableCompoundButton).supportButtonTintList = colorStateList
    }

    fun selectColor(){

        //colorPicker.removeAllViews()

        val selectColor = requireActivity().findViewById<Button>(R.id.selectColor)
        picker = requireActivity().findViewById(R.id.picker) as ColorPicker
        val svBar = requireActivity().findViewById(R.id.svbar) as SVBar
        picker.addSVBar(svBar)

        picker.onColorChangedListener = ColorPicker.OnColorChangedListener{
            color = picker.getColor()
        }


        val setColor = requireActivity().findViewById<Button>(R.id.setRange)
        val from = requireActivity().findViewById<EditText>(R.id.rangeFrom)
        val to = requireActivity().findViewById<EditText>(R.id.rangeTo)
        val stepSize = requireActivity().findViewById<EditText>(R.id.stepSize)

        setColor.setOnClickListener(){
            var start: Int = 0
            var end: Int = 0
            var step: Int = 1
            try {
                Log.e("Range", from.text.toString() + " " + to.text.toString())
                start = Integer.parseInt("0"+ from.text.toString())
                end = Integer.parseInt("0"+ to.text.toString())
                step = Integer.parseInt("0"+ stepSize.text.toString())
                if(end < start) end = start

                if(start >= leds.size) start = 0
                if(end >= leds.size) end = leds.size-1
                if(step >= leds.size) step = 1

                if(start < 0) start = 0
                if(end < 0) end = 0
                if(step <= 0) step = 1


            }catch (e: Exception){

            }
            Log.e("Range", start.toString() + " " + end.toString())
            for(i in start..end){
                if((i-start)%step == 0) {
                    leds.elementAt(i).setTag("on")
                    setAppCompatRadioButtonColor(
                        leds.elementAt(i),
                        Color.parseColor("#8A8A8A"),
                        color
                    )
                    leds.elementAt(i).isChecked = true
                    this.stripeLed.get(i).color = color
                    this.stripeLed.get(i).state = true
                    this.stripeLed.get(i).changed = true
                }
            }

        }


    }

    fun loadData(): MutableMap<Int, Stripe> {
        var map = mutableMapOf<Int, Stripe>()
        for (i in 0..10){
            var l = Vector<Int>()
            val rnd = Random()
            for(n in 0..rnd.nextInt(144)){
                var color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                l.addElement(color)
            }
            map.put(i, Stripe(i, "Test"+ i.toString(), l))
        }


        return map
    }
}