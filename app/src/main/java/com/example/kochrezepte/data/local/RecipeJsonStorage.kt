package com.example.kochrezepte.data.local

import android.content.Context
import com.example.kochrezepte.data.model.RecipeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class RecipeJsonStorage(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val dbFile: File
        get() = File(context.filesDir, "recipes_database.json")

    suspend fun load(): RecipeDatabase = withContext(Dispatchers.IO) {
        if (!dbFile.exists()) return@withContext RecipeDatabase()
        try {
            json.decodeFromString(RecipeDatabase.serializer(), dbFile.readText())
        } catch (e: Exception) {
            e.printStackTrace()
            RecipeDatabase()
        }
    }

    suspend fun save(database: RecipeDatabase) = withContext(Dispatchers.IO) {
        try {
            dbFile.writeText(json.encodeToString(database))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
