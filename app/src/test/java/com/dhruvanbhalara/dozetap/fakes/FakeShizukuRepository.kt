package com.dhruvanbhalara.dozetap.fakes

import com.dhruvanbhalara.dozetap.domain.repository.IShizukuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of [IShizukuRepository] for unit testing.
 *
 * @property isRunning Initial mock state for Shizuku binder service.
 * @property isGranted Initial mock state for Shizuku API permission.
 * @property shouldGrantSucceed Mock outcome for [grantWriteSettingsPermission].
 */
class FakeShizukuRepository(
    var isRunning: Boolean = true,
    var isGranted: Boolean = true,
    var shouldGrantSucceed: Boolean = true
) : IShizukuRepository {

    private val _availabilityFlow = MutableStateFlow(isRunning && isGranted)
    override val isShizukuAvailableFlow: StateFlow<Boolean> = _availabilityFlow.asStateFlow()

    var lastRequestedCode: Int? = null
        private set

    var grantCallCount: Int = 0
        private set

    override fun isShizukuRunning(): Boolean = isRunning

    override fun isShizukuPermissionGranted(): Boolean = isRunning && isGranted

    override fun requestShizukuPermission(requestCode: Int) {
        lastRequestedCode = requestCode
    }

    var onGrantAction: (() -> Unit)? = null

    override suspend fun grantWriteSettingsPermission(): Boolean {
        grantCallCount++
        return if (isRunning && isGranted) {
            onGrantAction?.invoke()
            shouldGrantSucceed
        } else {
            false
        }
    }

    fun updateState(running: Boolean, granted: Boolean) {
        isRunning = running
        isGranted = granted
        _availabilityFlow.value = running && granted
    }
}
