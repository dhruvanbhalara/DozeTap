package com.dhruvanbhalara.dozetap.domain.repository

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for interacting with system screen off timeout settings.
 */
interface ITimeoutRepository {
    /**
     * Checks if the app has permission to write system settings ([android.provider.Settings.System.canWrite]).
     *
     * @return `true` if write settings permission is granted, `false` otherwise.
     */
    fun canWriteSettings(): Boolean

    /**
     * Synchronously queries the current system screen off timeout.
     *
     * @return The active [TimeoutOption].
     */
    fun getCurrentTimeout(): TimeoutOption

    /**
     * Observes real-time system screen off timeout changes via a [android.database.ContentObserver].
     *
     * @return A cold [Flow] emitting updated [TimeoutOption]s on changes.
     */
    fun observeCurrentTimeout(): Flow<TimeoutOption>

    /**
     * Sets the system screen off timeout duration.
     *
     * @param option The target [TimeoutOption].
     * @return `true` if setting modification succeeded, `false` otherwise.
     */
    suspend fun setTimeout(option: TimeoutOption): Boolean

    /**
     * Retrieves the previously active timeout duration prior to the latest modification.
     *
     * @return The previous [TimeoutOption], or `null` if none recorded.
     */
    suspend fun getPreviousTimeout(): TimeoutOption?

    /**
     * Restores the system screen off timeout to the previously recorded timeout.
     *
     * @return `true` if restoration succeeded, `false` otherwise.
     */
    suspend fun restorePreviousTimeout(): Boolean
}
