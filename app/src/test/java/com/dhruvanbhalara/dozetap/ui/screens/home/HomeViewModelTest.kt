package com.dhruvanbhalara.dozetap.ui.screens.home

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.usecase.GetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.SetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.ToggleKeepScreenOnUseCase
import com.dhruvanbhalara.dozetap.fakes.FakePlatformSystemManager
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.fakes.FakeShizukuRepository
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Unit tests for [HomeViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createHomeViewModel(
        timeoutRepo: FakeTimeoutRepository,
        prefsRepo: FakePreferencesRepository,
        shizukuRepo: FakeShizukuRepository = FakeShizukuRepository(),
        platformManager: FakePlatformSystemManager = FakePlatformSystemManager()
    ): HomeViewModel {
        return HomeViewModel(
            timeoutRepository = timeoutRepo,
            preferencesRepository = prefsRepo,
            shizukuRepository = shizukuRepo,
            getScreenTimeoutUseCase = GetScreenTimeoutUseCase(timeoutRepo),
            setScreenTimeoutUseCase = SetScreenTimeoutUseCase(timeoutRepo, prefsRepo),
            toggleKeepScreenOnUseCase = ToggleKeepScreenOnUseCase(timeoutRepo, prefsRepo),
            platformSystemManager = platformManager
        )
    }

    /** Verifies that initial uiState properly loads system timeout from repository. */
    @Test
    fun `initial uiState loads current system timeout`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.TWO_MIN)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.TWO_MIN, viewModel.uiState.value.currentSystemTimeout)
        assertEquals(TimeoutOption.TWO_MIN, viewModel.uiState.value.selectedOption)
    }

    /** Verifies that selecting a timeout option updates selectedOption state in uiState. */
    @Test
    fun `selectTimeoutOption updates selected option in uiState`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectTimeoutOption(TimeoutOption.TEN_MIN)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.TEN_MIN, viewModel.uiState.value.selectedOption)
    }

    /** Verifies that refreshing state re-queries system permission and timeout. */
    @Test
    fun `refreshState updates write permission status`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = true)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canWriteSettings)

        timeoutRepo.canWrite = false
        viewModel.refreshState()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canWriteSettings)
    }

    /** Verifies Shizuku availability updates in uiState when Shizuku repository is present. */
    @Test
    fun `uiState reflects Shizuku running and permission status`() = runTest {
        val timeoutRepo = FakeTimeoutRepository()
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository(isRunning = true, isGranted = false)
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo, shizukuRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isShizukuRunning)
        assertFalse(viewModel.uiState.value.isShizukuPermissionGranted)
    }

    /** Verifies attempting Shizuku grant when Shizuku service is stopped emits warning toast effect. */
    @Test
    fun `grantPermissionWithShizuku emits toast effect when Shizuku not running`() = runTest {
        val timeoutRepo = FakeTimeoutRepository()
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository(isRunning = false)
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo, shizukuRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.grantPermissionWithShizuku()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(emittedEffect is HomeUiEffect.ShowToast)
        val toast = emittedEffect as HomeUiEffect.ShowToast
        assertTrue(toast.message.contains("Shizuku is not running"))
    }

    /** Verifies successful Shizuku permission grant updates permission state and emits toast. */
    @Test
    fun `grantPermissionWithShizuku grants write settings when Shizuku permission authorized`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = false)
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository(isRunning = true, isGranted = true)
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo, shizukuRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.grantPermissionWithShizuku()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(emittedEffect is HomeUiEffect.ShowToast)
        val toast = emittedEffect as HomeUiEffect.ShowToast
        assertTrue(toast.message.contains("granted via Shizuku"))
    }

    /** Verifies applyTimeout requests write settings permission effect when permission missing. */
    @Test
    fun `applyTimeout emits permission request effect when WRITE_SETTINGS missing`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = false)
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo, shizukuRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.applyTimeout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(HomeUiEffect.RequestWriteSettingsPermission, emittedEffect)
    }

    /** Verifies applyTimeout successfully updates system timeout when permission granted. */
    @Test
    fun `applyTimeout updates timeout settings when WRITE_SETTINGS granted`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = true, initialTimeout = TimeoutOption.FIFTEEN_SEC)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectTimeoutOption(TimeoutOption.FIVE_MIN)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.applyTimeout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.FIVE_MIN, timeoutRepo.getCurrentTimeout())
    }

    /** Verifies toggleKeepScreenOn switches to NEVER option when active. */
    @Test
    fun `toggleKeepScreenOn enables keep awake NEVER option`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = true, initialTimeout = TimeoutOption.FIFTEEN_SEC)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleKeepScreenOn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.NEVER, timeoutRepo.getCurrentTimeout())
        assertTrue(viewModel.uiState.value.isKeepScreenOnActive)
    }

    /** Verifies toggleKeepScreenOn restores previous timeout when already in NEVER mode. */
    @Test
    fun `toggleKeepScreenOn restores previous timeout when currently NEVER`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = true, initialTimeout = TimeoutOption.FIFTEEN_SEC)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleKeepScreenOn()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TimeoutOption.NEVER, timeoutRepo.getCurrentTimeout())

        viewModel.toggleKeepScreenOn()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TimeoutOption.FIFTEEN_SEC, timeoutRepo.getCurrentTimeout())
        assertFalse(viewModel.uiState.value.isKeepScreenOnActive)
    }

    /** Verifies restorePreviousTimeout restores previous system timeout option. */
    @Test
    fun `restorePreviousTimeout restores previous option state`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = true, initialTimeout = TimeoutOption.FIFTEEN_SEC)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        timeoutRepo.setTimeout(TimeoutOption.TEN_MIN)
        viewModel.restorePreviousTimeout()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TimeoutOption.FIFTEEN_SEC, timeoutRepo.getCurrentTimeout())
    }

    /** Verifies onAddTileClicked emits quick settings tile dialog instructions effect. */
    @Test
    fun `onAddTileClicked emits ShowAddTileInstructions effect`() = runTest {
        val timeoutRepo = FakeTimeoutRepository()
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.onAddTileClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(HomeUiEffect.ShowAddTileInstructions, emittedEffect)
    }

    /** Verifies onAddWidgetClicked emits RequestAddWidget effect. */
    @Test
    fun `onAddWidgetClicked emits RequestAddWidget effect`() = runTest {
        val timeoutRepo = FakeTimeoutRepository()
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.onAddWidgetClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(HomeUiEffect.RequestAddWidget, emittedEffect)
    }

    /** Verifies grantPermissionWithShizuku auto-updates canWriteSettings to true when granted. */
    @Test
    fun `grantPermissionWithShizuku updates canWriteSettings to true and hides banner`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = false)
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository(isRunning = true, isGranted = true, shouldGrantSucceed = true)
        shizukuRepo.onGrantAction = { timeoutRepo.canWrite = true }
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo, shizukuRepo)

        backgroundScope.launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canWriteSettings)

        viewModel.grantPermissionWithShizuku()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canWriteSettings)
    }

    /** Verifies selecting timeout preset when write permission is missing emits RequestWriteSettingsPermission effect. */
    @Test
    fun `selectTimeoutOption when write permission missing emits RequestWriteSettingsPermission effect`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = false)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.selectTimeoutOption(TimeoutOption.FIVE_MIN)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(HomeUiEffect.RequestWriteSettingsPermission, emittedEffect)
    }

    /** Verifies toggling keep screen on when write permission is missing emits RequestWriteSettingsPermission effect. */
    @Test
    fun `toggleKeepScreenOn when write permission missing emits RequestWriteSettingsPermission effect`() = runTest {
        val timeoutRepo = FakeTimeoutRepository(canWrite = false)
        val prefsRepo = FakePreferencesRepository()
        val viewModel = createHomeViewModel(timeoutRepo, prefsRepo)

        var emittedEffect: HomeUiEffect? = null
        backgroundScope.launch {
            viewModel.uiEffect.collect { emittedEffect = it }
        }

        viewModel.toggleKeepScreenOn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(HomeUiEffect.RequestWriteSettingsPermission, emittedEffect)
    }
}
