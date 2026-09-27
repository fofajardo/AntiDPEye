package com.fofajardo.antidpeye.utility

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.fofajardo.antidpeye.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

private const val TAG = "LogUtils"

fun collectLogs(): String? =
    try {
        Runtime
            .getRuntime()
            .exec("logcat *:D -d")
            .inputStream
            .bufferedReader()
            .use { it.readText() }
    } catch (e: Exception) {
        null
    }

suspend fun saveLogsToUri(
    context: Context,
    uri: Uri,
) {
    withContext(Dispatchers.IO) {
        val logs = collectLogs()
        if (logs == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.logs_failed, Toast.LENGTH_SHORT).show()
            }
            return@withContext
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(logs.toByteArray())
            } ?: run {
                Log.e(TAG, "Failed to open output stream")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save logs", e)
        }
    }
}
