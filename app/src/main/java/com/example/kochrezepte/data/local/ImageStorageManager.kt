package com.example.kochrezepte.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.content.FileProvider

class ImageStorageManager(private val context: Context) {

    private val imagesDir: File by lazy {
        File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
    }

    fun saveImage(uri: Uri): String? {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val destFile = File(imagesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            File(context.cacheDir, "camera_temp").listFiles()?.forEach { it.delete() }
        }
    }

    fun createCameraCaptureUri(): Uri {
        val tempDir = File(context.cacheDir, "camera_temp").apply { if (!exists()) mkdirs() }
        val tempFile = File(tempDir, "${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    fun getImageFile(fileName: String?): File? {
        if (fileName.isNullOrBlank()) return null
        val file = File(imagesDir, fileName)
        return if (file.exists()) file else null
    }

    fun deleteImage(fileName: String?) {
        if (fileName.isNullOrBlank()) return
        File(imagesDir, fileName).takeIf { it.exists() }?.delete()
    }

    fun deleteAllImages() {
        imagesDir.listFiles()?.forEach { it.delete() }
    }
}
