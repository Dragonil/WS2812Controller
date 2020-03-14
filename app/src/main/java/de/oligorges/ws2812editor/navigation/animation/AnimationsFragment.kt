package de.oligorges.ws2812editor.navigation.animation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.oligorges.ws2812editor.R
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView


class AnimationsFragment : Fragment() {

    private lateinit var animationsViewModel: AnimationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animationsViewModel =
            ViewModelProvider(this).get(AnimationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_animation, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get Stripe
        var id:Int? = arguments?.getInt("stripeID")
        if(id == null) id = 0

        testAnimation(listOf<Int>(Color.parseColor("#0000FF"), Color.parseColor("#FF00FF"), Color.parseColor("#00FFF0"), Color.parseColor("#FFF000")))

    }

    fun testAnimation(colors: List<Int>){
        var button = requireActivity().findViewById<Button>(R.id.play)
        button.setOnClickListener { v ->
            val img = requireActivity().findViewById(R.id.animation) as ImageView
            img.setBackgroundResource(R.drawable.test_animation)

            // Get the background, which has been compiled to an AnimationDrawable object.
            val frameAnimation = img.getBackground() as AnimationDrawable
            //var obj: ObjectAnimator = ObjectAnimator.

            // Start the animation (looped playback by default).
            frameAnimation.start()
        }
    }
}