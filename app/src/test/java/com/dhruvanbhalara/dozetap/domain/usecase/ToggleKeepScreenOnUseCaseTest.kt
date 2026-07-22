package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [ToggleKeepScreenOnUseCase].
 */
class ToggleKeepScreenOnUseCaseTest {

    /** Verifies that toggling when current timeout is standard preset sets timeout to NEVER. */
    @Test
    fun `invoke toggles from standard timeout to NEVER`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val prefsRepo = FakePreferencesRepository()
        val useCase = ToggleKeepScreenOnUseCase(timeoutRepo, prefsRepo)

        val result = useCase()

        assertEquals(TimeoutOption.NEVER, result)
        assertEquals(TimeoutOption.NEVER, timeoutRepo.getCurrentTimeout())

        val recents = prefsRepo.getRecentTimeouts().first()
        assertEquals(TimeoutOption.NEVER, recents.first())
    }

    /** Verifies that toggling when current timeout is NEVER restores previous timeout. */
    @Test
    fun `invoke toggles from NEVER back to previous timeout`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.FIVE_MIN)
        val prefsRepo = FakePreferencesRepository()
        val useCase = ToggleKeepScreenOnUseCase(timeoutRepo, prefsRepo)

        // Set to NEVER first
        useCase()
        assertEquals(TimeoutOption.NEVER, timeoutRepo.getCurrentTimeout())

        // Toggle again to restore
        val restored = useCase()

        assertEquals(TimeoutOption.FIVE_MIN, restored)
        assertEquals(TimeoutOption.FIVE_MIN, timeoutRepo.getCurrentTimeout())
    }

    /** Verifies that toggling from NEVER falls back to THIRTY_SEC when no previous timeout is recorded. */
    @Test
    fun `invoke toggles from NEVER to THIRTY_SEC when no previous timeout exists`() = runTest {
        // Start directly at NEVER — FakeTimeoutRepository has no previousOption recorded
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.NEVER)
        val prefsRepo = FakePreferencesRepository()
        val useCase = ToggleKeepScreenOnUseCase(timeoutRepo, prefsRepo)

        val result = useCase()

        assertEquals(TimeoutOption.THIRTY_SEC, result)
        assertEquals(TimeoutOption.THIRTY_SEC, timeoutRepo.getCurrentTimeout())
    }
}
