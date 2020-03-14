package de.oligorges.ws2812editor.navigation.home

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.oligorges.ws2812editor.R
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import de.oligorges.ws2812editor.Room.stripe
import kotlinx.android.synthetic.main.fragment_home.*




class HomeFragment : Fragment(){

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var stripes: List<stripe>
    private val defaultColor = Color.parseColor("#8A8A8A")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(HomeViewModel::class.java)

        homeViewModel.allStripes.observe(this.viewLifecycleOwner, Observer {
            stripes = it
            showData()
        })
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id:Int? = arguments?.getInt("stripeID")
        if(id != null && id > 0 ){
            Snackbar.make(view, "LED Added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        fab.setOnClickListener { v ->
            this.findNavController().navigate(R.id.nav_new)
        }

    }


    fun showData(){
        val table: TableLayout = requireActivity().findViewById(R.id.homePanels)
        var row = TableRow(this.context)
        for (i in 0..stripes.size-1){
            val stripe: stripe = stripes.elementAt(i)
            Log.e("Button", stripe.name)

            var orientation = requireActivity().getResources().getConfiguration().orientation
            var e = 2
            if(orientation == Configuration.ORIENTATION_LANDSCAPE)
                e = 3

            if (i%e == 0){
                row = TableRow(this.context)
                val param = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    0.5f
                )
                row.setLayoutParams(param)
                table.addView(row)
            }

            var linearLayout = LinearLayout(this.context)
            linearLayout.orientation = LinearLayout.VERTICAL
            val param = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                600,
                0.5f
            )
            param.setMargins(10,10,10,20)
            linearLayout.setLayoutParams(param)

            var button = ImageButton(this.context)
            val param2 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
            )
            param2.setMargins(10,10,10,10)
            button.setLayoutParams(param2)
            button.setImageResource(R.drawable.ic_power_settings_new_black_64dp);

            button.setTag("off")
            button.setBackgroundColor(defaultColor)

            homeViewModel.getStatus(stripe, button)

            // Part of Strip
            button.setOnClickListener {
                homeViewModel.toogleStrip(stripe)
                if(button.getTag().toString() == "on"){
                    button.setBackgroundColor(defaultColor)
                    button.setTag("off")
                }
                else{
                    button.setBackgroundColor(stripe.color)
                    button.setTag("on")
                }
            }
            button.setOnLongClickListener{
                var bundle = bundleOf("stripeID" to stripe.uid)
                this.findNavController().navigate(R.id.nav_edit, bundle)
                true
            }

            var text = TextView(this.context)
            text.text = stripe.name.toUpperCase()
            text.width = LinearLayout.LayoutParams.MATCH_PARENT
            text.gravity = Gravity.CENTER
            text.setTypeface(Typeface.DEFAULT_BOLD)
            text.setOnClickListener{
                var bundle = bundleOf("stripeID" to stripe.uid)
                this.findNavController().navigate(R.id.nav_edit, bundle)
                true
            }

            linearLayout.addView(button)
            linearLayout.addView(text)

            row.addView(linearLayout)


        }

        if (stripes.size%2 != 0){
            var placeholder = LinearLayout(this.context)
            val param = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                0.5f
            )
            placeholder.setLayoutParams(param)
            row.addView(placeholder)
        }

    }

    val selectLED = View.OnClickListener() {

    }
    fun loadData() {



    }


}