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


    fun setCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri
    }

    fun saveImageWithEmoji( context: Context, bitmap: Bitmap,folderName : String): Uri? {
        return ImageRepository.saveBitmapToPublicPictures( context, bitmap,folderName)
    }
}