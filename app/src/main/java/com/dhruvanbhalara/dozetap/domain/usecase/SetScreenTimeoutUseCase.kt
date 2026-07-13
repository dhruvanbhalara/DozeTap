package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import javax.inject.Inject

/**
 * Use case for changing the system screen off timeout.
 *
 * @property timeoutRepository The repository managing system screen off timeout.
 * @property preferencesRepository The repository persisting recent presets history.
 */
class SetScreenTimeoutUseCase @Inject constructor(
    private val timeoutRepository: ITimeoutRepository,
    private val preferencesRepository: IPreferencesRepository
) {
    /**
     * Applies the specified screen timeout option.
     *
     * @param option The target [TimeoutOption].
     * @return `true` if system setting update succeeded, `false` otherwise.
     */
    suspend operator fun invoke(option: TimeoutOption): Boolean {
        val success = timeoutRepository.setTimeout(option)
        if (success) {
            preferencesRepository.addRecentTimeout(option)
        }
        return success
    }
}
