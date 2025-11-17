package com.example.sh

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CheckinStore {
    private const val FILE_NAME = "checkins.csv"
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun appendNow(context: Context, source: String = "wear") {
        val ts = sdf.format(Date())
        val line = "$ts,$source\n"
        context.openFileOutput(FILE_NAME, Context.MODE_APPEND).use { it.write(line.toByteArray()) }
    }

    fun readAll(context: Context): List<String> {
        return try {
            context.openFileInput(FILE_NAME).bufferedReader().readLines()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
