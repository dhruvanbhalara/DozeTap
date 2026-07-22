package com.dhruvanbhalara.dozetap.data.repository

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests verifying [TimeoutRepositoryImpl] delegation logic using [FakeTimeoutRepository]
 * as a proxy.
 *
 * Note: [TimeoutRepositoryImpl] methods that call [android.provider.Settings.System] or
 * [android.content.ContentResolver] (canWriteSettings, getCurrentTimeout, setTimeout,
 * observeCurrentTimeout) require an Android [android.content.Context] and must be covered
 * by instrumented tests in the androidTest source set.
 *
 * The tests here verify the observable contract of the interface, guarding that
 * FakeTimeoutRepository correctly models the behaviour expected from the real implementation.
 */
class TimeoutRepositoryImplTest {

    /** Verifies that getPreviousTimeout returns null before any timeout change is recorded. */
    @Test
    fun `getPreviousTimeout returns null when no previous timeout is stored`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)

        val prev = fakeRepo.getPreviousTimeout()

        assertNull(prev)
    }

    /** Verifies that getPreviousTimeout returns the stored timeout after a change is applied. */
    @Test
    fun `getPreviousTimeout returns stored timeout after a different timeout is applied`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        fakeRepo.setTimeout(TimeoutOption.FIVE_MIN)

        val prev = fakeRepo.getPreviousTimeout()

        assertEquals(TimeoutOption.THIRTY_SEC, prev)
    }

    /** Verifies that restorePreviousTimeout returns false when no previous timeout is stored. */
    @Test
    fun `restorePreviousTimeout returns false when no previous timeout is stored`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)

        val result = fakeRepo.restorePreviousTimeout()

        assertFalse(result)
    }

    /** Verifies that restorePreviousTimeout returns true and restores the previous timeout. */
    @Test
    fun `restorePreviousTimeout returns true and restores timeout when previous exists`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        fakeRepo.setTimeout(TimeoutOption.TEN_MIN)

        val result = fakeRepo.restorePreviousTimeout()

        assertTrue(result)
        assertEquals(TimeoutOption.THIRTY_SEC, fakeRepo.getCurrentTimeout())
    }
}
