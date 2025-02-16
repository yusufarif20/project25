package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Rect

class GamePizza : AppCompatActivity(), View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f
    private var counter = 0
    private lateinit var textView: TextView
    private lateinit var keranjang: ImageView
    private lateinit var submitButton: ImageView
    private var droppedViews = mutableSetOf<ImageView>()
    private var viewValues = mutableMapOf<Int, Int>()
    private val maxObjects = 4
    private var isDraggable = true
    private val targetValue = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_pizza)

        textView = findViewById(R.id.TextView)
        val pizza1 = findViewById<ImageView>(R.id.pizza1)
        val pizza2 = findViewById<ImageView>(R.id.pizza2)
        val pizza3 = findViewById<ImageView>(R.id.pizza3)
        val pizza4 = findViewById<ImageView>(R.id.pizza4)
        keranjang = findViewById(R.id.keranjang)
        submitButton = findViewById(R.id.submit)
        var monster = intent.getIntExtra("monster", 0)

        // Set touch listeners
        pizza1.setOnTouchListener(this)
        pizza2.setOnTouchListener(this)
        pizza3.setOnTouchListener(this)
        pizza4.setOnTouchListener(this)

        // Initialize values for each view
        viewValues[R.id.pizza1] = 1
        viewValues[R.id.pizza2] = 1
        viewValues[R.id.pizza3] = 1
        viewValues[R.id.pizza4] = 1

        // Initialize counter
        textView.text = "0"

        submitButton.setOnClickListener {
            if (counter == targetValue) {
                val intent = Intent(this, rute::class.java)
                monster -= 1
                intent.putExtra("monster", monster)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Pecahan Belum Benar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val imageView = view as ImageView

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if basket is full and the view is not already in the basket
                if (droppedViews.size >= maxObjects && !droppedViews.contains(imageView)) {
                    Toast.makeText(
                        this,
                        "Keranjang sudah penuh! Kurangi objek terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false // Prevent dragging
                }
                // Allow dragging for objects in basket or when basket isn't full
                dX = view.x - event.rawX
                dY = view.y - event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Only move the view if it's either in the basket or the basket isn't full
                if (droppedViews.contains(imageView) || droppedViews.size < maxObjects) {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
            }
            MotionEvent.ACTION_UP -> {
                val value = viewValues[view.id] ?: 0

                if (isViewOverlapping(imageView, keranjang)) {
                    if (!droppedViews.contains(imageView) && droppedViews.size < maxObjects) {
                        // Add object if within limit
                        counter += value
                        textView.text = counter.toString()
                        droppedViews.add(imageView)
                    }
                } else if (!isViewOverlapping(imageView, keranjang) && droppedViews.contains(imageView)) {
                    // Remove object from basket
                    counter -= value
                    textView.text = counter.toString()
                    droppedViews.remove(imageView)
                }
            }
        }
        return true
    }

    private fun isViewOverlapping(view1: ImageView, view2: ImageView): Boolean {
        val rect1 = Rect()
        val rect2 = Rect()

        view1.getGlobalVisibleRect(rect1)
        view2.getGlobalVisibleRect(rect2)

        return Rect.intersects(rect1, rect2)
    }
}