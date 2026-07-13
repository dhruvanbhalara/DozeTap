package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import javax.inject.Inject

/**
 * Use case for toggling Keep Screen On mode.
 *
 * @property timeoutRepository Repository for system screen off timeout operations.
 * @property preferencesRepository Repository for persisting previous timeout state.
 */
class ToggleKeepScreenOnUseCase @Inject constructor(
    private val timeoutRepository: ITimeoutRepository,
    private val preferencesRepository: IPreferencesRepository
) {
    /**
     * Toggles screen timeout between [TimeoutOption.NEVER] and the previous duration.
     *
     * @return The newly applied [TimeoutOption].
     */
    suspend operator fun invoke(): TimeoutOption {
        val current = timeoutRepository.getCurrentTimeout()
        val targetOption = if (current.milliseconds == TimeoutOption.NEVER.milliseconds) {
            timeoutRepository.getPreviousTimeout() ?: TimeoutOption.THIRTY_SEC
        } else {
            TimeoutOption.NEVER
        }
        
        timeoutRepository.setTimeout(targetOption)
        preferencesRepository.addRecentTimeout(targetOption)
        return targetOption
    }
}
