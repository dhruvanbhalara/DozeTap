package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

/**
 * Use case for querying and observing active system screen off timeout.
 *
 * @property timeoutRepository The repository for querying system timeout settings.
 */
class GetScreenTimeoutUseCase @Inject constructor(
    private val timeoutRepository: ITimeoutRepository
) {
    /**
     * Synchronously returns the currently active screen timeout option.
     *
     * @return The active [TimeoutOption].
     */
    operator fun invoke(): TimeoutOption {
        return timeoutRepository.getCurrentTimeout()
    }

    /**
     * Returns a [Flow] observing live system timeout updates.
     *
     * @return Cold flow of [TimeoutOption].
     */
    fun observe(): Flow<TimeoutOption> {
        return timeoutRepository.observeCurrentTimeout()
    }
}
