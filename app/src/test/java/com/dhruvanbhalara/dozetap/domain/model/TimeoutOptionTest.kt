package com.dhruvanbhalara.dozetap.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [TimeoutOption] domain model.
 */
class TimeoutOptionTest {

    /** Verifies that exact millisecond values match pre-defined preset options. */
    @Test
    fun `fromMilliseconds returns exact preset match when available`() {
        assertEquals(TimeoutOption.FIFTEEN_SEC, TimeoutOption.fromMilliseconds(15_000))
        assertEquals(TimeoutOption.THIRTY_SEC, TimeoutOption.fromMilliseconds(30_000))
        assertEquals(TimeoutOption.ONE_MIN, TimeoutOption.fromMilliseconds(60_000))
        assertEquals(TimeoutOption.TWO_MIN, TimeoutOption.fromMilliseconds(120_000))
        assertEquals(TimeoutOption.FIVE_MIN, TimeoutOption.fromMilliseconds(300_000))
        assertEquals(TimeoutOption.TEN_MIN, TimeoutOption.fromMilliseconds(600_000))
        assertEquals(TimeoutOption.THIRTY_MIN, TimeoutOption.fromMilliseconds(1_800_000))
        assertEquals(TimeoutOption.NEVER, TimeoutOption.fromMilliseconds(Int.MAX_VALUE))
    }

    /** Verifies that values exceeding 24 hours or Int.MAX_VALUE resolve to NEVER. */
    @Test
    fun `fromMilliseconds returns NEVER for extreme boundary values`() {
        assertEquals(TimeoutOption.NEVER, TimeoutOption.fromMilliseconds(86_400_000))
        assertEquals(TimeoutOption.NEVER, TimeoutOption.fromMilliseconds(100_000_000))
        assertEquals(TimeoutOption.NEVER, TimeoutOption.fromMilliseconds(Int.MAX_VALUE))
    }

    /** Verifies that intermediate millisecond values resolve to the nearest preset. */
    @Test
    fun `fromMilliseconds returns nearest preset match for non-standard durations`() {
        assertEquals(TimeoutOption.FIFTEEN_SEC, TimeoutOption.fromMilliseconds(10_000))
        assertEquals(TimeoutOption.THIRTY_SEC, TimeoutOption.fromMilliseconds(25_000))
        assertEquals(TimeoutOption.ONE_MIN, TimeoutOption.fromMilliseconds(50_000))
    }

    /** Verifies that zero and very small millisecond inputs resolve to the nearest preset without crashing. */
    @Test
    fun `fromMilliseconds handles zero and near-zero inputs gracefully`() {
        assertEquals(TimeoutOption.FIFTEEN_SEC, TimeoutOption.fromMilliseconds(0))
        assertEquals(TimeoutOption.FIFTEEN_SEC, TimeoutOption.fromMilliseconds(1))
        assertEquals(TimeoutOption.FIFTEEN_SEC, TimeoutOption.fromMilliseconds(1_000))
    }
}
