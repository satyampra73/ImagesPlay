package com.satyam.imageplay.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageRepository {
    companion object{
        fun saveBitmapToPublicPictures(
            context: Context,
            bitmap: Bitmap,
            folderName: String = "MySavedImages"
        ): Uri? {
            val filename = "IMG_${System.currentTimeMillis()}.png"
            val mime = "image/png"
            val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + folderName

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri ?: return null

            try {
                resolver.openOutputStream(uri)?.use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        Log.e("SaveImage", "Compression failed")
                    }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                Log.d("SaveImage", "Saved to $relativeLocation/$filename")
                return uri
            } catch (e: IOException) {
                Log.e("SaveImage", "Failed to save image", e)
                return null
            }
        }
    }
}