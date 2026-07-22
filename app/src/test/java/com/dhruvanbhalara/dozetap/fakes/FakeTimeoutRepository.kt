package com.dhruvanbhalara.dozetap.fakes

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake implementation of [ITimeoutRepository] for unit testing.
 *
 * @param initialTimeout Initial screen timeout preset.
 * @param canWrite `true` if write settings permission is mocked as granted.
 */
class FakeTimeoutRepository(
    initialTimeout: TimeoutOption = TimeoutOption.THIRTY_SEC,
    var canWrite: Boolean = true
) : ITimeoutRepository {

    private val currentTimeoutFlow = MutableStateFlow(initialTimeout)
    private var previousOption: TimeoutOption? = null

    override fun canWriteSettings(): Boolean = canWrite

    override fun getCurrentTimeout(): TimeoutOption = currentTimeoutFlow.value

    override fun observeCurrentTimeout(): Flow<TimeoutOption> = currentTimeoutFlow

    override suspend fun setTimeout(option: TimeoutOption): Boolean {
        if (!canWrite) return false
        val old = currentTimeoutFlow.value
        if (old.milliseconds != option.milliseconds) {
            previousOption = old
        }
        currentTimeoutFlow.value = option
        return true
    }

    override suspend fun getPreviousTimeout(): TimeoutOption? = previousOption

    override suspend fun restorePreviousTimeout(): Boolean {
        val prev = previousOption ?: return false
        return setTimeout(prev)
    }
}
