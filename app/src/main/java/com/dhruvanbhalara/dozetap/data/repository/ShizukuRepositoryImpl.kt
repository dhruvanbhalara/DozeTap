package com.dhruvanbhalara.dozetap.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.dhruvanbhalara.dozetap.domain.repository.IShizukuRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IShizukuRepository] using official Shizuku API binder wrapper and process execution.
 *
 * @property context Application context.
 * @property externalScope Coroutine scope for reactive state updates.
 */
@Singleton
class ShizukuRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val externalScope: CoroutineScope
) : IShizukuRepository {

    private val _isShizukuAvailableFlow = MutableStateFlow(checkAvailability())
    override val isShizukuAvailableFlow: StateFlow<Boolean> = _isShizukuAvailableFlow.asStateFlow()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        updateAvailability()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        updateAvailability()
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _, _ ->
        updateAvailability()
    }

    init {
        try {
            Shizuku.addBinderReceivedListener(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(permissionResultListener)
        } catch (_: Throwable) {
            // Ignored if Shizuku binder environment is unavailable
        }
        updateAvailability()
    }

    override fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (_: Throwable) {
            false
        }
    }

    override fun isShizukuPermissionGranted(): Boolean {
        return try {
            if (!isShizukuRunning()) return false
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) {
            false
        }
    }

    override fun requestShizukuPermission(requestCode: Int) {
        try {
            if (isShizukuRunning() && !isShizukuPermissionGranted()) {
                Shizuku.requestPermission(requestCode)
            }
        } catch (_: Throwable) {
            // Ignored
        }
    }

    override suspend fun grantWriteSettingsPermission(): Boolean = withContext(Dispatchers.IO) {
        if (!isShizukuRunning() || !isShizukuPermissionGranted()) {
            return@withContext false
        }

        return@withContext try {
            val packageName = context.packageName
            val method = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            method.isAccessible = true
            val process = method.invoke(
                null,
                arrayOf("appops", "set", packageName, "WRITE_SETTINGS", "allow"),
                null,
                null
            ) as java.lang.Process
            val exitCode = process.waitFor()
            updateAvailability()
            exitCode == 0
        } catch (_: Throwable) {
            false
        }
    }

    private fun checkAvailability(): Boolean {
        return isShizukuRunning() && isShizukuPermissionGranted()
    }

    private fun updateAvailability() {
        externalScope.launch {
            _isShizukuAvailableFlow.value = checkAvailability()
        }
    }
}
