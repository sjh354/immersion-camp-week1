package com.example.test1

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import android.app.Activity
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator


class MainActivity : Activity() {
    private lateinit var textView: TextView
    private var offset = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.helloText)

        // Start the rainbow animation
        animateRainbow()
    }

    private fun animateRainbow() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 3000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            offset = animation.animatedValue as Float
            updateRainbowGradient()
        }

        animator.start()
    }

    private fun updateRainbowGradient() {
        textView.post {
            val width = textView.width.toFloat()
            if (width > 0) {
                val colors = intArrayOf(
                    0xFFFF0000.toInt(), // Red
                    0xFFFF7F00.toInt(), // Orange
                    0xFFFFFF00.toInt(), // Yellow
                    0xFF00FF00.toInt(), // Green
                    0xFF0000FF.toInt(), // Blue
                    0xFF4B0082.toInt(), // Indigo
                    0xFF9400D3.toInt(), // Violet
                    0xFFFF0000.toInt()  // Red (to loop smoothly)
                )

                val shift = offset * width * 2
                val shader = LinearGradient(
                    -shift, 0f, width - shift, 0f,
                    colors, null,
                    Shader.TileMode.CLAMP
                )

                textView.paint.shader = shader
                textView.invalidate()
            }
        }
    }
}