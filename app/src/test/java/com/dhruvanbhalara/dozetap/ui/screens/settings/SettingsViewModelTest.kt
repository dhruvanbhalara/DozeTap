package com.dhruvanbhalara.dozetap.ui.screens.settings

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.fakes.FakePlatformSystemManager
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Unit tests for [SettingsViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** Verifies that updating default timeout preset updates uiState. */
    @Test
    fun `setDefaultTimeout updates preference in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDefaultTimeout(TimeoutOption.FIVE_MIN)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.FIVE_MIN, viewModel.uiState.value.defaultTimeout)
    }

    /** Verifies that setting dark theme configuration updates uiState. */
    @Test
    fun `setDarkThemeConfig updates dark mode configuration`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDarkThemeConfig(DarkThemeConfig.DARK)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DarkThemeConfig.DARK, viewModel.uiState.value.darkThemeConfig)
    }

    /** Verifies that updating dynamic color state updates uiState. */
    @Test
    fun `setDynamicColorEnabled updates dynamic color preference in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDynamicColorEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isDynamicColorEnabled)
    }

    /** Verifies that updating Quick Settings tile cycle order updates uiState. */
    @Test
    fun `setCycleOrder updates tile cycle order in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)
        val customOrder = listOf(TimeoutOption.ONE_MIN, TimeoutOption.TEN_MIN)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setCycleOrder(customOrder)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(customOrder, viewModel.uiState.value.cycleOrder)
    }

    /** Verifies that updating show text labels preference updates uiState. */
    @Test
    fun `setShowTextLabelsEnabled updates text label preference in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setShowTextLabelsEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.showTextLabels)
    }

    /** Verifies that updating vibrate on change preference updates uiState. */
    @Test
    fun `setVibrateOnChangeEnabled updates haptic feedback preference in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setVibrateOnChangeEnabled(false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.vibrateOnChange)
    }

    /** Verifies that setting app language updates appLanguage in uiState. */
    @Test
    fun `setAppLanguage updates language tag in uiState`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setAppLanguage("hi")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("hi", viewModel.uiState.value.appLanguage)
    }

    /** Verifies that requestAddTile delegates call to platformSystemManager. */
    @Test
    fun `requestAddTile delegates call to platformSystemManager`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        val result = viewModel.requestAddTile()

        assertTrue(result)
        assertTrue(platformSystemManager.isAddTileCalled)
    }

    /** Verifies that initial uiState matches FakePreferencesRepository default values on ViewModel creation. */
    @Test
    fun `initial uiState matches FakePreferencesRepository defaults`() = runTest {
        val prefsRepo = FakePreferencesRepository()
        val platformSystemManager = FakePlatformSystemManager()
        val viewModel = SettingsViewModel(prefsRepo, platformSystemManager)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.THIRTY_SEC, viewModel.uiState.value.defaultTimeout)
        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, viewModel.uiState.value.darkThemeConfig)
        assertEquals(true, viewModel.uiState.value.isDynamicColorEnabled)
        assertEquals(true, viewModel.uiState.value.showTextLabels)
        assertEquals(true, viewModel.uiState.value.vibrateOnChange)
        assertEquals("", viewModel.uiState.value.appLanguage)
        assertEquals(TimeoutOption.DEFAULT_PRESETS, viewModel.uiState.value.cycleOrder)
    }
}
