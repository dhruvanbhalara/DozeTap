package com.dhruvanbhalara.dozetap.data.repository

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests verifying [PreferencesRepositoryImpl] expected behaviour using
 * [FakePreferencesRepository] as a proxy.
 *
 * Note: [PreferencesRepositoryImpl] depends on Jetpack DataStore which requires an Android
 * [android.content.Context] and cannot be unit tested on the JVM. DataStore integration
 * must be covered by instrumented tests in the androidTest source set.
 *
 * The tests here verify the full contract of [com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository],
 * ensuring the Fake correctly models all expected behaviours and guarding against fake drift.
 */
class PreferencesRepositoryImplTest {

    /** Verifies that getRecentTimeouts default list contains the expected initial presets. */
    @Test
    fun `getRecentTimeouts default contains expected initial presets`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        val recents = fakeRepo.getRecentTimeouts().first()

        assertEquals(listOf(TimeoutOption.FIFTEEN_SEC, TimeoutOption.TWO_MIN, TimeoutOption.TEN_MIN), recents)
    }

    /** Verifies that addRecentTimeout prepends the new option and deduplicates existing entries. */
    @Test
    fun `addRecentTimeout prepends and deduplicates entries`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.addRecentTimeout(TimeoutOption.FIVE_MIN)

        val recents = fakeRepo.getRecentTimeouts().first()
        assertEquals(TimeoutOption.FIVE_MIN, recents.first())
        assertEquals(1, recents.count { it.milliseconds == TimeoutOption.FIVE_MIN.milliseconds })
    }

    /** Verifies that addRecentTimeout trims the list to a maximum of 3 entries. */
    @Test
    fun `addRecentTimeout trims list to max 3 entries`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.addRecentTimeout(TimeoutOption.THIRTY_MIN)
        fakeRepo.addRecentTimeout(TimeoutOption.NEVER)

        val recents = fakeRepo.getRecentTimeouts().first()
        assertEquals(3, recents.size)
    }

    /** Verifies that getCycleOrder returns the full DEFAULT_PRESETS list on initial state. */
    @Test
    fun `getCycleOrder returns DEFAULT_PRESETS initially`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        val order = fakeRepo.getCycleOrder().first()

        assertEquals(TimeoutOption.DEFAULT_PRESETS, order)
    }

    /** Verifies that setCycleOrder correctly updates the cycle order observable. */
    @Test
    fun `setCycleOrder updates the observable cycle order`() = runTest {
        val fakeRepo = FakePreferencesRepository()
        val customOrder = listOf(TimeoutOption.ONE_MIN, TimeoutOption.TEN_MIN)

        fakeRepo.setCycleOrder(customOrder)

        assertEquals(customOrder, fakeRepo.getCycleOrder().first())
    }

    /** Verifies that getPreviousTimeout returns null before any previous timeout is set. */
    @Test
    fun `getPreviousTimeout returns null initially`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        val prev = fakeRepo.getPreviousTimeout().first()

        assertNull(prev)
    }

    /** Verifies that setPreviousTimeout and getPreviousTimeout roundtrip correctly. */
    @Test
    fun `setPreviousTimeout and getPreviousTimeout roundtrip correctly`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setPreviousTimeout(TimeoutOption.FIVE_MIN)
        val prev = fakeRepo.getPreviousTimeout().first()

        assertEquals(TimeoutOption.FIVE_MIN, prev)
    }

    /** Verifies that setPreviousTimeout with null clears the stored previous timeout. */
    @Test
    fun `setPreviousTimeout with null clears the stored value`() = runTest {
        val fakeRepo = FakePreferencesRepository()
        fakeRepo.setPreviousTimeout(TimeoutOption.FIVE_MIN)

        fakeRepo.setPreviousTimeout(null)

        assertNull(fakeRepo.getPreviousTimeout().first())
    }

    /** Verifies that setOnboardingCompleted updates the observable state. */
    @Test
    fun `setOnboardingCompleted updates the observable state`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        assertFalse(fakeRepo.isOnboardingCompleted().first())
        fakeRepo.setOnboardingCompleted(true)
        assertTrue(fakeRepo.isOnboardingCompleted().first())
    }

    /** Verifies that setDarkThemeConfig updates the observable dark theme state. */
    @Test
    fun `setDarkThemeConfig updates the observable dark theme state`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setDarkThemeConfig(DarkThemeConfig.AMOLED)

        assertEquals(DarkThemeConfig.AMOLED, fakeRepo.getDarkThemeConfig().first())
    }

    /** Verifies that setDynamicColorEnabled updates the observable dynamic color state. */
    @Test
    fun `setDynamicColorEnabled updates the observable dynamic color state`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setDynamicColorEnabled(false)

        assertFalse(fakeRepo.isDynamicColorEnabled().first())
    }

    /** Verifies that setDefaultTimeout updates the observable default timeout state. */
    @Test
    fun `setDefaultTimeout updates the observable default timeout state`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setDefaultTimeout(TimeoutOption.TEN_MIN)

        assertEquals(TimeoutOption.TEN_MIN, fakeRepo.getDefaultTimeout().first())
    }

    /** Verifies that setShowTextLabelsEnabled updates the observable text labels preference. */
    @Test
    fun `setShowTextLabelsEnabled updates the observable text labels preference`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setShowTextLabelsEnabled(false)

        assertFalse(fakeRepo.isShowTextLabelsEnabled().first())
    }

    /** Verifies that setVibrateOnChangeEnabled updates the observable haptic feedback preference. */
    @Test
    fun `setVibrateOnChangeEnabled updates the observable haptic feedback preference`() = runTest {
        val fakeRepo = FakePreferencesRepository()

        fakeRepo.setVibrateOnChangeEnabled(false)

        assertFalse(fakeRepo.isVibrateOnChangeEnabled().first())
    }
}
