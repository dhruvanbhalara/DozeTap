package com.dhruvanbhalara.dozetap.util

import android.app.PendingIntent
import android.app.StatusBarManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.dhruvanbhalara.dozetap.R
import com.dhruvanbhalara.dozetap.service.DozeTapTileService
import com.dhruvanbhalara.dozetap.widget.DozeTapWidgetReceiver
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction layer for interacting with Android system APIs (Status bar tiles, app widgets, permission intents).
 */
interface PlatformSystemManager {
    /** Requests pinning the Quick Settings tile natively on Android 13+. */
    fun requestAddTileNative(): Boolean

    /** Requests pinning the DozeTap home screen widget natively on Android 8.0+. */
    fun requestAddWidget(): Boolean

    /** Generates an intent to open system WRITE_SETTINGS settings screen. */
    fun getWriteSettingsPermissionIntent(): Intent
}

/**
 * Implementation of [PlatformSystemManager] accessing Android framework system services.
 */
@Singleton
class PlatformSystemManagerImpl @Inject constructor(
    private val context: Context
) : PlatformSystemManager {

    override fun requestAddTileNative(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val statusBarManager = context.getSystemService(StatusBarManager::class.java)
            val tileComponent = ComponentName(context, DozeTapTileService::class.java)
            statusBarManager?.requestAddTileService(
                tileComponent,
                "DozeTap",
                android.graphics.drawable.Icon.createWithResource(context, R.drawable.ic_qs_tile_dozetap),
                { _ -> },
                { _ -> }
            )
            return true
        }
        return false
    }

    override fun requestAddWidget(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val myProvider = ComponentName(context, DozeTapWidgetReceiver::class.java)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, DozeTapWidgetReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                return true
            }
        }
        return false
    }

    override fun getWriteSettingsPermissionIntent(): Intent {
        return Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
