package com.satyam.imageplay.view

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import androidx.lifecycle.lifecycleScope
import com.satyam.imageplay.viewmodel.CustomProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class PreviewActivity : AppCompatActivity() {
    private lateinit var rootLayout: FrameLayout
    private lateinit var imageCaptured: ImageView
    private lateinit var btnAddEmoji: Button
    private lateinit var btnSaveImage: Button
    private lateinit var cameraViewModel: CameraViewModel

    private var emojiView: ImageView? = null
    private var imageUri: Uri? = null

    private val progressBar = CustomProgressBar()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        rootLayout = findViewById(R.id.rootLayout)
        imageCaptured = findViewById(R.id.imageCaptured)
        btnAddEmoji = findViewById(R.id.btnAddEmoji)
        btnSaveImage = findViewById(R.id.btnSaveImage)

        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }
        imageCaptured.setImageURI(imageUri)

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

        btnSaveImage.setOnClickListener {
            progressBar.showProgress(this@PreviewActivity)

            lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.Default) {
                    getBitmapFromView(rootLayout)
                }

                val savedUri = withContext(Dispatchers.IO) {
                    cameraViewModel.saveImageWithEmoji(this@PreviewActivity, bitmap, "MySavedImages")
                }

                progressBar.hideProgress()
                Toast.makeText(this@PreviewActivity, "Saved at: $savedUri", Toast.LENGTH_SHORT).show()
                Log.d("LogImage", "Saved at: $savedUri")
            }


        }

    }


    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


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




}