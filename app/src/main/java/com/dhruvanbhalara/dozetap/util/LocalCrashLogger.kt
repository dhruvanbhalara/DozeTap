package com.dhruvanbhalara.dozetap.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Local privacy-focused crash logger for capturing unhandled exceptions off the network.
 * Stores non-sensitive exception summaries in app-private storage for diagnostic export.
 */
object LocalCrashLogger {

    private const val LOG_FILENAME = "dozetap_crash_logs.txt"
    private const val MAX_LOG_SIZE_BYTES = 512 * 1024L // 512 KB

    /**
     * Initializes global default uncaught exception handler.
     *
     * @param context Application context.
     */
    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logException(context, thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Appends an exception trace to the local log file.
     */
    fun logException(context: Context, thread: Thread, throwable: Throwable) {
        try {
            val logFile = File(context.filesDir, LOG_FILENAME)
            if (logFile.length() > MAX_LOG_SIZE_BYTES) {
                logFile.delete()
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            val logEntry = buildString {
                appendLine("=== CRASH RECORD [$timestamp] ===")
                appendLine("Thread: ${thread.name} (ID: ${thread.id})")
                appendLine("Exception: ${throwable.javaClass.name}: ${throwable.message}")
                appendLine("Stack Trace:")
                throwable.stackTrace.take(15).forEach { element ->
                    appendLine("    at $element")
                }
                appendLine()
            }

            logFile.appendText(logEntry)
        } catch (_: Throwable) {
            // Ignore logging errors to prevent recursive crashes
        }
    }

    /**
     * Retrieves recorded local crash log content.
     */
    fun getCrashLogs(context: Context): String {
        val logFile = File(context.filesDir, LOG_FILENAME)
        return if (logFile.exists()) logFile.readText() else "No crash logs recorded."
    }

    /**
     * Clears recorded crash log file.
     */
    fun clearLogs(context: Context) {
        val logFile = File(context.filesDir, LOG_FILENAME)
        if (logFile.exists()) {
            logFile.delete()
        }
    }
}
