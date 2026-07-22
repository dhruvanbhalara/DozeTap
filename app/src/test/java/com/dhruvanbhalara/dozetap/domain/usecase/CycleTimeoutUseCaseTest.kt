package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [CycleTimeoutUseCase]. */
class CycleTimeoutUseCaseTest {

    /** Verifies that cycling timeout steps to the next configured preset. */
    @Test
    fun `cycleToNext steps to next preset in order`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.FIFTEEN_SEC)
        val prefsRepo = FakePreferencesRepository()
        val useCase = CycleTimeoutUseCase(timeoutRepo, prefsRepo)

        val next = useCase.cycleToNext()

        assertEquals(TimeoutOption.THIRTY_SEC, next)
        assertEquals(TimeoutOption.THIRTY_SEC, timeoutRepo.getCurrentTimeout())
    }

    /** Verifies that cycling timeout wraps around to the first preset when at the end of the preset list. */
    @Test
    fun `cycleToNext wraps around to first preset when at last preset`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.NEVER)
        val prefsRepo = FakePreferencesRepository()
        val useCase = CycleTimeoutUseCase(timeoutRepo, prefsRepo)

        val next = useCase.cycleToNext()

        assertEquals(TimeoutOption.FIFTEEN_SEC, next)
        assertEquals(TimeoutOption.FIFTEEN_SEC, timeoutRepo.getCurrentTimeout())
    }

    /** Verifies that cycling timeout respects custom cycle order configured in preferences repository. */
    @Test
    fun `cycleToNext respects custom configured cycle order`() = runTest {
        val customOrder = listOf(TimeoutOption.ONE_MIN, TimeoutOption.TEN_MIN)
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.ONE_MIN)
        val prefsRepo = FakePreferencesRepository()
        prefsRepo.setCycleOrder(customOrder)

        val useCase = CycleTimeoutUseCase(timeoutRepo, prefsRepo)
        val next = useCase.cycleToNext()

        assertEquals(TimeoutOption.TEN_MIN, next)
    }

    /** Verifies that cycleToNext does not add to recent timeouts when write settings permission is denied. */
    @Test
    fun `cycleToNext does not add to recent timeouts when canWrite is false`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(
            initialTimeout = TimeoutOption.FIFTEEN_SEC,
            canWrite = false
        )
        val prefsRepo = FakePreferencesRepository()
        val originalRecents = prefsRepo.getRecentTimeouts().first()
        val useCase = CycleTimeoutUseCase(timeoutRepo, prefsRepo)

        useCase.cycleToNext()

        val recentsAfter = prefsRepo.getRecentTimeouts().first()
        assertEquals(originalRecents, recentsAfter)
    }

    /** Verifies that getNextTimeout returns the first preset when the current timeout is not found in the cycle list. */
    @Test
    fun `getNextTimeout returns first preset when current timeout is not in cycle list`() = runTest {
        val customOrder = listOf(TimeoutOption.ONE_MIN, TimeoutOption.TEN_MIN)
        // TWO_MIN is not in the custom cycle order
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.TWO_MIN)
        val prefsRepo = FakePreferencesRepository()
        prefsRepo.setCycleOrder(customOrder)
        val useCase = CycleTimeoutUseCase(timeoutRepo, prefsRepo)

        val next = useCase.getNextTimeout()

        assertEquals(TimeoutOption.ONE_MIN, next)
    }
}
