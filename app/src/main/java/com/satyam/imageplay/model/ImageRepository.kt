package com.satyam.imageplay.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class ImageRepository {
    companion object{
        fun saveImageToStorage(bitmap: Bitmap, context: Context): Uri {
            val filename = "IMG_${System.currentTimeMillis()}.jpg"
            val dir = File(context.getExternalFilesDir(null), "SavedImages")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, filename)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return Uri.fromFile(file)
        }
    }
}