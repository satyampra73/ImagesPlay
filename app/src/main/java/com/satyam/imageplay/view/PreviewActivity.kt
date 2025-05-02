package com.satyam.imageplay.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.satyam.imageplay.R
import com.satyam.imageplay.viewmodel.CameraViewModel
import androidx.core.graphics.createBitmap

class PreviewActivity : AppCompatActivity() {
    private lateinit var rootLayout: FrameLayout
    private lateinit var imageCaptured: ImageView
    private lateinit var btnAddEmoji: Button
    private lateinit var btnSaveImage: Button
    private lateinit var cameraViewModel: CameraViewModel

    private var emojiView: ImageView? = null
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        rootLayout = findViewById(R.id.rootLayout)
        imageCaptured = findViewById(R.id.imageCaptured)
        btnAddEmoji = findViewById(R.id.btnAddEmoji)
        btnSaveImage = findViewById(R.id.btnSaveImage)

        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        // Load captured image
        imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }
        imageCaptured.setImageURI(imageUri)

        // Add Emoji Button
        btnAddEmoji.setOnClickListener {
            if (emojiView == null) {
                emojiView = ImageView(this).apply {
                    setImageResource(R.drawable.smile)
                    layoutParams = FrameLayout.LayoutParams(150, 150).apply {
                        gravity = Gravity.CENTER
                    }
                    setOnTouchListener(DragTouchListener())
                }
                rootLayout.addView(emojiView)
            }
        }

//        // Save Image Button
//        btnSaveImage.setOnClickListener {
//            val bitmap = getBitmapFromView(rootLayout)
//            val savedUri = cameraViewModel.saveImageWithEmoji(bitmap, this)
//            Toast.makeText(this, "Saved at: $savedUri", Toast.LENGTH_SHORT).show()
//            Log.d("LogImage", "Saved at: $savedUri")
//        }


        btnSaveImage.setOnClickListener {
            val finalBitmap = mergeCapturedImageWithEmoji()
            val savedUri = cameraViewModel.saveImageWithEmoji(finalBitmap, this)
            Toast.makeText(this, "Saved at: $savedUri", Toast.LENGTH_SHORT).show()
        }

    }

    // Convert layout to Bitmap
    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    // Touch Listener for dragging emoji
    inner class DragTouchListener : View.OnTouchListener {
        private var dX = 0f
        private var dY = 0f

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.x = event.rawX + dX
                    view.y = event.rawY + dY
                }
            }
            return true
        }
    }

    private fun mergeCapturedImageWithEmoji(): Bitmap {
        // 1. Get base bitmap from captured image
        imageCaptured.isDrawingCacheEnabled = true
        val capturedBitmap = Bitmap.createBitmap(imageCaptured.drawingCache)
        imageCaptured.isDrawingCacheEnabled = false

        // 2. Create a mutable bitmap to draw emoji on top
        val resultBitmap = createBitmap(capturedBitmap.width, capturedBitmap.height)
        val canvas = Canvas(resultBitmap)
        canvas.drawBitmap(capturedBitmap, 0f, 0f, null)

        // 3. Draw the emojiView at its current position
        emojiView?.let { emoji ->
            // Get emoji bitmap
            emoji.isDrawingCacheEnabled = true
            val emojiBitmap = Bitmap.createBitmap(emoji.drawingCache)
            emoji.isDrawingCacheEnabled = false

            // Calculate emoji position relative to root layout
            val location = IntArray(2)
            emoji.getLocationOnScreen(location)
            val emojiX = emoji.x
            val emojiY = emoji.y

            canvas.drawBitmap(emojiBitmap, emojiX, emojiY, null)
        }

        return resultBitmap
    }

}