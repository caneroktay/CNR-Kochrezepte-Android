package com.example.kochrezepte.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Kamera/galeriden seçilen resimleri uygulamanın kendi özel klasörüne
 * (context.filesDir/images) kopyalar. İnternet gerektirmez, dosyalar
 * uygulama silinene kadar cihazda kalır.
 */
class ImageStorageManager(private val context: Context) {

    private val imagesDir: File by lazy {
        File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
    }

    /**
     * Verilen [uri] içeriğini images/ klasörüne kopyalar ve
     * sadece dosya adını (örn: "a1b2c3.jpg") döndürür.
     * Bu dosya adı Recipe.imageFileName alanına kaydedilir.
     */
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
        }
    }

    /** Kayıtlı dosya adından tam dosya yolunu (File) üretir. */
    fun getImageFile(fileName: String?): File? {
        if (fileName.isNullOrBlank()) return null
        val file = File(imagesDir, fileName)
        return if (file.exists()) file else null
    }

    /** Tarif/kategori silindiğinde ilişkili resmi de diskten siler. */
    fun deleteImage(fileName: String?) {
        if (fileName.isNullOrBlank()) return
        File(imagesDir, fileName).takeIf { it.exists() }?.delete()
    }

    /** Tüm verileri sil (Ayarlar > Alle Daten löschen) için. */
    fun deleteAllImages() {
        imagesDir.listFiles()?.forEach { it.delete() }
    }
}
