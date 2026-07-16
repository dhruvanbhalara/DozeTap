package com.dhruvanbhalara.dozetap.data.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [ITimeoutRepository] for managing Android system screen off timeout settings.
 *
 * Handles system [Settings.System.SCREEN_OFF_TIMEOUT] queries and updates off the UI thread,
 * observing changes via [android.database.ContentObserver].
 *
 * @property context The application context used for content resolver operations.
 * @property preferencesRepository Repository used to persist previous timeout state across Process Death.
 */
@Singleton
class TimeoutRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: IPreferencesRepository
) : ITimeoutRepository {

    override fun canWriteSettings(): Boolean {
        return Settings.System.canWrite(context)
    }

    override fun getCurrentTimeout(): TimeoutOption {
        return try {
            val ms = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
            )
            TimeoutOption.fromMilliseconds(ms)
        } catch (e: Exception) {
            TimeoutOption.THIRTY_SEC
        }
    }

    override fun observeCurrentTimeout(): Flow<TimeoutOption> = callbackFlow {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : android.database.ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                trySend(getCurrentTimeout())
            }
        }

        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT),
            false,
            observer
        )

        trySend(getCurrentTimeout())

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun setTimeout(option: TimeoutOption): Boolean = withContext(Dispatchers.IO) {
        if (!canWriteSettings()) return@withContext false
        val current = getCurrentTimeout()
        if (current.milliseconds != option.milliseconds) {
            preferencesRepository.setPreviousTimeout(current)
        }

        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                option.milliseconds
            )
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getPreviousTimeout(): TimeoutOption? {
        return preferencesRepository.getPreviousTimeout().firstOrNull()
    }

    override suspend fun restorePreviousTimeout(): Boolean {
        val prev = getPreviousTimeout() ?: return false
        return setTimeout(prev)
    }
}
