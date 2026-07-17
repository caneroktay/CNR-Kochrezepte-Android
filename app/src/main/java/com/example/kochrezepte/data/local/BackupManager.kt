package com.example.kochrezepte.data.local

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(private val context: Context) {

    private val dbFile: File get() = File(context.filesDir, "recipes_database.json")
    private val imagesDir: File get() = File(context.filesDir, "images")

    suspend fun exportBackup(destinationUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(destinationUri)?.use { out ->
                ZipOutputStream(out).use { zip ->
                    if (dbFile.exists()) {
                        zip.putNextEntry(ZipEntry("recipes_database.json"))
                        dbFile.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                    imagesDir.listFiles()?.forEach { imgFile ->
                        zip.putNextEntry(ZipEntry("images/${imgFile.name}"))
                        imgFile.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }
            } ?: return@withContext false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importBackup(sourceUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!imagesDir.exists()) imagesDir.mkdirs()
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        val name = entry.name
                        val outFile = when {
                            name == "recipes_database.json" -> dbFile
                            name.startsWith("images/") -> File(imagesDir, name.removePrefix("images/"))
                            else -> null
                        }
                        if (outFile != null && !entry.isDirectory) {
                            outFile.outputStream().use { output -> zip.copyTo(output) }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: return@withContext false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromAssets(assetFileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!imagesDir.exists()) imagesDir.mkdirs()
            context.assets.open(assetFileName).use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        val name = entry.name
                        val outFile = when {
                            name == "recipes_database.json" -> dbFile
                            name.startsWith("images/") -> File(imagesDir, name.removePrefix("images/"))
                            else -> null
                        }
                        if (outFile != null && !entry.isDirectory) {
                            outFile.outputStream().use { output -> zip.copyTo(output) }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



}