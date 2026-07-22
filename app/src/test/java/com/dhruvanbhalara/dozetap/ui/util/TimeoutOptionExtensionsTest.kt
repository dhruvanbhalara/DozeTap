package com.dhruvanbhalara.dozetap.ui.util

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests verifying duration classification logic for localized timeout labels.
 *
 * Ensures range boundaries map to the expected unit bucket (seconds, minutes, hours, never).
 */
class TimeoutOptionExtensionsTest {

    @Test
    fun `fifteen seconds option maps to seconds bucket`() {
        val option = TimeoutOption.FIFTEEN_SEC
        val seconds = option.milliseconds / 1000
        assertEquals(15, seconds)
    }

    @Test
    fun `thirty seconds option maps to seconds bucket`() {
        val option = TimeoutOption.THIRTY_SEC
        val seconds = option.milliseconds / 1000
        assertEquals(30, seconds)
    }

    @Test
    fun `one minute option maps to minutes bucket`() {
        val option = TimeoutOption.ONE_MIN
        val minutes = option.milliseconds / 60_000
        assertEquals(1, minutes)
    }

    @Test
    fun `two minutes option maps to minutes bucket`() {
        val option = TimeoutOption.TWO_MIN
        val minutes = option.milliseconds / 60_000
        assertEquals(2, minutes)
    }

    @Test
    fun `five minutes option maps to minutes bucket`() {
        val option = TimeoutOption.FIVE_MIN
        val minutes = option.milliseconds / 60_000
        assertEquals(5, minutes)
    }

    @Test
    fun `ten minutes option maps to minutes bucket`() {
        val option = TimeoutOption.TEN_MIN
        val minutes = option.milliseconds / 60_000
        assertEquals(10, minutes)
    }

    @Test
    fun `thirty minutes option maps to minutes bucket`() {
        val option = TimeoutOption.THIRTY_MIN
        val minutes = option.milliseconds / 60_000
        assertEquals(30, minutes)
    }

    @Test
    fun `never option maps to max int value`() {
        val option = TimeoutOption.NEVER
        assertEquals(Int.MAX_VALUE, option.milliseconds)
    }
}
