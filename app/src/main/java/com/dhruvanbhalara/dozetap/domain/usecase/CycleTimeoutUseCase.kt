package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for cycling through timeout presets.
 *
 * @property timeoutRepository Repository for screen timeout operations.
 * @property preferencesRepository Repository for recording timeout preset history.
 */
class CycleTimeoutUseCase @Inject constructor(
    private val timeoutRepository: ITimeoutRepository,
    private val preferencesRepository: IPreferencesRepository
) {
    /**
     * Calculates the next [TimeoutOption] in the configured cycle sequence without applying it.
     *
     * @return The next [TimeoutOption] in sequence.
     */
    suspend fun getNextTimeout(): TimeoutOption {
        val current = timeoutRepository.getCurrentTimeout()
        val cycleList = preferencesRepository.getCycleOrder().first()
        val currentIndex = cycleList.indexOfFirst { it.milliseconds == current.milliseconds }
        
        return if (currentIndex == -1 || currentIndex == cycleList.lastIndex) {
            cycleList.firstOrNull() ?: TimeoutOption.THIRTY_SEC
        } else {
            cycleList[currentIndex + 1]
        }
    }

    /**
     * Cycles the system screen timeout to the next option in sequence and records it in recent history.
     *
     * @return The newly applied [TimeoutOption].
     */
    suspend fun cycleToNext(): TimeoutOption {
        val next = getNextTimeout()
        val success = timeoutRepository.setTimeout(next)
        if (success) {
            preferencesRepository.addRecentTimeout(next)
        }
        return next
    }
}
