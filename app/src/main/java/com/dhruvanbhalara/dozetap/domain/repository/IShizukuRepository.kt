package com.dhruvanbhalara.dozetap.domain.repository

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for querying Shizuku service availability, permission state, and executing elevated system permission grants.
 */
interface IShizukuRepository {
    /**
     * Checks whether the Shizuku manager service is currently running on the device.
     *
     * @return `true` if Shizuku binder is alive and responsive, `false` otherwise.
     */
    fun isShizukuRunning(): Boolean

    /**
     * Checks whether DozeTap has been authorized to use the Shizuku API by the user.
     *
     * @return `true` if Shizuku API permission is granted, `false` otherwise.
     */
    fun isShizukuPermissionGranted(): Boolean

    /**
     * Observable stream emitting real-time updates to Shizuku availability and authorization states.
     */
    val isShizukuAvailableFlow: StateFlow<Boolean>

    /**
     * Requests authorization from the Shizuku manager service.
     *
     * @param requestCode Arbitrary request identifier code.
     */
    fun requestShizukuPermission(requestCode: Int)

    /**
     * Grants the system `WRITE_SETTINGS` permission to DozeTap via elevated Shizuku shell execution.
     *
     * @return `true` if permission grant execution succeeded, `false` otherwise.
     */
    suspend fun grantWriteSettingsPermission(): Boolean
}
