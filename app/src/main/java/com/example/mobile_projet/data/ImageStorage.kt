package com.example.mobile_projet.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageStorage {
    private const val BACKGROUND_IMAGE_NAME = "profile_background.jpg"
    private const val AVATAR_IMAGE_NAME = "profile_avatar.jpg"
    
    fun saveBackgroundImage(context: Context, uri: Uri): Boolean {
        return saveImage(context, uri, BACKGROUND_IMAGE_NAME)
    }
    
    fun saveAvatarImage(context: Context, uri: Uri): Boolean {
        return saveImage(context, uri, AVATAR_IMAGE_NAME)
    }
    
    private fun saveImage(context: Context, uri: Uri, fileName: String): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    fun getBackgroundImageFile(context: Context): File? {
        return getImageFile(context, BACKGROUND_IMAGE_NAME)
    }
    
    fun getAvatarImageFile(context: Context): File? {
        return getImageFile(context, AVATAR_IMAGE_NAME)
    }
    
    private fun getImageFile(context: Context, fileName: String): File? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file else null
    }
}

