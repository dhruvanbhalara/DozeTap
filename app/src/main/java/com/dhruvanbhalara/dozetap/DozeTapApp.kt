package com.dhruvanbhalara.dozetap

import android.app.Application
import com.dhruvanbhalara.dozetap.util.LocalCrashLogger
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for DozeTap.
 * Configured with [HiltAndroidApp] for automated dependency injection.
 */
@HiltAndroidApp
class DozeTapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalCrashLogger.install(this)
    }
}
