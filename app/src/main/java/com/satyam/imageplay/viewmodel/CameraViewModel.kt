package com.satyam.imageplay.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.satyam.imageplay.model.ImageRepository

class CameraViewModel : ViewModel(){
    private val _capturedImageUri = MutableLiveData<Uri>()
    val capturedImageUri: LiveData<Uri> = _capturedImageUri

    fun setCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri
    }

    fun saveImageWithEmoji(bitmap: Bitmap, context: Context): Uri {
        return ImageRepository.saveImageToStorage(bitmap, context)
    }
}