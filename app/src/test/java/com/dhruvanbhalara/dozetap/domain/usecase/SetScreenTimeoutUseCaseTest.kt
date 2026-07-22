package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for [SetScreenTimeoutUseCase]. */
class SetScreenTimeoutUseCaseTest {

    /** Verifies that setting screen timeout updates system state and adds option to recent timeouts. */
    @Test
    fun `invoke updates system timeout and adds to recent timeouts`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val prefsRepo = FakePreferencesRepository()
        val useCase = SetScreenTimeoutUseCase(timeoutRepo, prefsRepo)

        val result = useCase(TimeoutOption.FIVE_MIN)

        assertTrue(result)
        assertEquals(TimeoutOption.FIVE_MIN, timeoutRepo.getCurrentTimeout())

        val recents = prefsRepo.getRecentTimeouts().first()
        assertEquals(TimeoutOption.FIVE_MIN, recents.first())
    }

    /** Verifies that setting timeout returns false and does not update recent history when write settings permission is denied. */
    @Test
    fun `invoke returns false and does not add to recent timeouts when canWrite is false`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(
            initialTimeout = TimeoutOption.THIRTY_SEC,
            canWrite = false
        )
        val prefsRepo = FakePreferencesRepository()
        val originalRecents = prefsRepo.getRecentTimeouts().first()
        val useCase = SetScreenTimeoutUseCase(timeoutRepo, prefsRepo)

        val result = useCase(TimeoutOption.FIVE_MIN)

        assertFalse(result)
        assertEquals(TimeoutOption.THIRTY_SEC, timeoutRepo.getCurrentTimeout())
        assertEquals(originalRecents, prefsRepo.getRecentTimeouts().first())
    }
}
