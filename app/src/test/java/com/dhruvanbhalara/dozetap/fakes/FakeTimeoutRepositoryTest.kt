package com.dhruvanbhalara.dozetap.fakes

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests for [FakeTimeoutRepository] verifying it correctly satisfies the
 * [com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository] interface contract.
 *
 * Guards against fake drift — ensures every method behaves as the real repository contract
 * specifies so that ViewModel and UseCase tests remain reliable.
 */
class FakeTimeoutRepositoryTest {

    /** Verifies canWriteSettings returns true when constructed with default canWrite=true. */
    @Test
    fun `canWriteSettings returns true by default`() {
        val fakeRepo = FakeTimeoutRepository()
        assertTrue(fakeRepo.canWriteSettings())
    }

    /** Verifies canWriteSettings returns false when constructed with canWrite=false. */
    @Test
    fun `canWriteSettings returns false when canWrite is false`() {
        val fakeRepo = FakeTimeoutRepository(canWrite = false)
        assertFalse(fakeRepo.canWriteSettings())
    }

    /** Verifies getCurrentTimeout returns the initial timeout preset passed to the constructor. */
    @Test
    fun `getCurrentTimeout returns initial value`() {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.FIVE_MIN)
        assertEquals(TimeoutOption.FIVE_MIN, fakeRepo.getCurrentTimeout())
    }

    /** Verifies that setTimeout updates the current timeout when write permission is granted. */
    @Test
    fun `setTimeout updates current timeout when canWrite is true`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC, canWrite = true)
        val success = fakeRepo.setTimeout(TimeoutOption.TEN_MIN)
        assertTrue(success)
        assertEquals(TimeoutOption.TEN_MIN, fakeRepo.getCurrentTimeout())
    }

    /** Verifies that setTimeout returns false and does not change timeout when write permission is denied. */
    @Test
    fun `setTimeout returns false and does not update when canWrite is false`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC, canWrite = false)
        val success = fakeRepo.setTimeout(TimeoutOption.TEN_MIN)
        assertFalse(success)
        assertEquals(TimeoutOption.THIRTY_SEC, fakeRepo.getCurrentTimeout())
    }

    /** Verifies that getPreviousTimeout returns null when no previous timeout has been recorded. */
    @Test
    fun `getPreviousTimeout returns null on first call before any change`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val prev = fakeRepo.getPreviousTimeout()
        assertNull(prev)
    }

    /** Verifies that getPreviousTimeout returns the old value after setTimeout applies a different timeout. */
    @Test
    fun `getPreviousTimeout returns previous value after setTimeout changes the timeout`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        fakeRepo.setTimeout(TimeoutOption.FIVE_MIN)
        val prev = fakeRepo.getPreviousTimeout()
        assertEquals(TimeoutOption.THIRTY_SEC, prev)
    }

    /** Verifies that restorePreviousTimeout returns false when no previous timeout exists. */
    @Test
    fun `restorePreviousTimeout returns false when no previous timeout is recorded`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val result = fakeRepo.restorePreviousTimeout()
        assertFalse(result)
        assertEquals(TimeoutOption.THIRTY_SEC, fakeRepo.getCurrentTimeout())
    }

    /** Verifies that restorePreviousTimeout restores the previous timeout and returns true when one exists. */
    @Test
    fun `restorePreviousTimeout restores and returns true when previous exists`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        fakeRepo.setTimeout(TimeoutOption.FIVE_MIN)
        val result = fakeRepo.restorePreviousTimeout()
        assertTrue(result)
        assertEquals(TimeoutOption.THIRTY_SEC, fakeRepo.getCurrentTimeout())
    }

    /** Verifies that observeCurrentTimeout emits the current value immediately on collection. */
    @Test
    fun `observeCurrentTimeout emits current value on collect`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.TWO_MIN)
        val emitted = fakeRepo.observeCurrentTimeout().first()
        assertEquals(TimeoutOption.TWO_MIN, emitted)
    }

    /** Verifies that setTimeout with the same value does not overwrite the recorded previous option. */
    @Test
    fun `setTimeout with same value does not record a new previous option`() = runTest {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        fakeRepo.setTimeout(TimeoutOption.FIVE_MIN)
        fakeRepo.setTimeout(TimeoutOption.FIVE_MIN)
        val prev = fakeRepo.getPreviousTimeout()
        assertEquals(TimeoutOption.THIRTY_SEC, prev)
    }
}
