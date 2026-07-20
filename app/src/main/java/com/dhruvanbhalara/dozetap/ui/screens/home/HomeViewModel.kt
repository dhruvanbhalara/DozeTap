package com.dhruvanbhalara.dozetap.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.IShizukuRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import com.dhruvanbhalara.dozetap.domain.usecase.GetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.SetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.ToggleKeepScreenOnUseCase
import com.dhruvanbhalara.dozetap.util.PlatformSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Home Dashboard screen.
 */
@Immutable
data class HomeUiState(
    val currentSystemTimeout: TimeoutOption = TimeoutOption.THIRTY_SEC,
    val selectedOption: TimeoutOption = TimeoutOption.THIRTY_SEC,
    val recentTimeouts: List<TimeoutOption> = emptyList(),
    val canWriteSettings: Boolean = true,
    val isKeepScreenOnActive: Boolean = false,
    val isShizukuRunning: Boolean = false,
    val isShizukuPermissionGranted: Boolean = false
)

/**
 * One-time side effects emitted by [HomeViewModel] for UI notifications and system interactions.
 */
sealed interface HomeUiEffect {
    data class ShowToast(val message: String) : HomeUiEffect
    object RequestWriteSettingsPermission : HomeUiEffect
    object ShowAddTileInstructions : HomeUiEffect
    object RequestAddWidget : HomeUiEffect
}

/**
 * ViewModel managing state and business logic for the Home Dashboard screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timeoutRepository: ITimeoutRepository,
    private val preferencesRepository: IPreferencesRepository,
    private val shizukuRepository: IShizukuRepository,
    private val getScreenTimeoutUseCase: GetScreenTimeoutUseCase,
    private val setScreenTimeoutUseCase: SetScreenTimeoutUseCase,
    private val toggleKeepScreenOnUseCase: ToggleKeepScreenOnUseCase,
    val platformSystemManager: PlatformSystemManager
) : ViewModel() {

    private val _selectedOption = MutableStateFlow<TimeoutOption?>(null)
    private val _permissionStateFlow = MutableStateFlow(timeoutRepository.canWriteSettings())
    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)

    /** One-time UI side effects flow guaranteed by buffered channel. */
    val uiEffect: Flow<HomeUiEffect> = _uiEffect.receiveAsFlow()

    private val shizukuFlow = shizukuRepository.isShizukuAvailableFlow

    /** Reactive UI state stream combining live system updates with user selection state and Shizuku status. */
    val uiState: StateFlow<HomeUiState> = combine(
        getScreenTimeoutUseCase.observe(),
        preferencesRepository.getRecentTimeouts(),
        _selectedOption,
        _permissionStateFlow,
        shizukuFlow
    ) { currentTimeout, recentTimeouts, userSelected, canWrite, _ ->
        val running = shizukuRepository.isShizukuRunning()
        val granted = shizukuRepository.isShizukuPermissionGranted()
        HomeUiState(
            currentSystemTimeout = currentTimeout,
            selectedOption = userSelected ?: currentTimeout,
            recentTimeouts = recentTimeouts,
            canWriteSettings = canWrite,
            isKeepScreenOnActive = currentTimeout.milliseconds == TimeoutOption.NEVER.milliseconds,
            isShizukuRunning = running,
            isShizukuPermissionGranted = granted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            currentSystemTimeout = TimeoutOption.THIRTY_SEC,
            selectedOption = TimeoutOption.THIRTY_SEC,
            canWriteSettings = true,
            isKeepScreenOnActive = false,
            isShizukuRunning = false,
            isShizukuPermissionGranted = false
        )
    )

    /**
     * Refreshes permission and timeout status on screen foreground events.
     */
    /**
     * Refreshes permission and timeout status on screen foreground events.
     * Polls for AppOps IPC propagation delay after returning from System Settings.
     */
    fun refreshState() {
        val canWrite = timeoutRepository.canWriteSettings()
        _permissionStateFlow.value = canWrite
        val current = getScreenTimeoutUseCase()
        _selectedOption.update { current }

        if (!canWrite) {
            viewModelScope.launch {
                val delays = listOf(100L, 250L, 500L, 1000L)
                for (delayMs in delays) {
                    kotlinx.coroutines.delay(delayMs)
                    if (timeoutRepository.canWriteSettings()) {
                        _permissionStateFlow.value = true
                        break
                    }
                }
            }
        }
    }

    private fun verifyPermission(): Boolean {
        val canWrite = timeoutRepository.canWriteSettings()
        if (canWrite) {
            _permissionStateFlow.value = true
            return true
        }
        viewModelScope.launch {
            _uiEffect.send(HomeUiEffect.RequestWriteSettingsPermission)
        }
        return false
    }

    /**
     * Selects a timeout preset in the UI without applying it to the system yet.
     */
    fun selectTimeoutOption(option: TimeoutOption) {
        if (!verifyPermission()) return
        _selectedOption.value = option
    }

    /**
     * Attempts to grant WRITE_SETTINGS permission using Shizuku elevated shell execution.
     */
    fun grantPermissionWithShizuku() {
        if (!shizukuRepository.isShizukuRunning()) {
            viewModelScope.launch {
                _uiEffect.send(HomeUiEffect.ShowToast("Shizuku is not running. Please start Shizuku service first."))
            }
            return
        }
        if (!shizukuRepository.isShizukuPermissionGranted()) {
            shizukuRepository.requestShizukuPermission(1001)
            return
        }

        viewModelScope.launch {
            val success = shizukuRepository.grantWriteSettingsPermission()
            if (success) {
                var granted = timeoutRepository.canWriteSettings()
                var retries = 0
                while (!granted && retries < 4) {
                    kotlinx.coroutines.delay(100)
                    granted = timeoutRepository.canWriteSettings()
                    retries++
                }
                refreshState()
                _uiEffect.send(HomeUiEffect.ShowToast("Permission granted via Shizuku!"))
            } else {
                _uiEffect.send(HomeUiEffect.ShowToast("Failed to grant permission via Shizuku"))
            }
        }
    }

    /**
     * Applies the currently selected timeout preset to system settings.
     */
    fun applyTimeout() {
        if (!verifyPermission()) return

        val target = uiState.value.selectedOption
        viewModelScope.launch {
            val success = setScreenTimeoutUseCase(target)
            if (success) {
                _selectedOption.value = target
                _uiEffect.send(HomeUiEffect.ShowToast("Screen timeout set to ${target.displayLabel}"))
            } else {
                _uiEffect.send(HomeUiEffect.ShowToast("Failed to change screen timeout"))
            }
        }
    }

    /**
     * Toggles between Keep Screen On ([TimeoutOption.NEVER]) and the previous timeout option.
     */
    fun toggleKeepScreenOn() {
        if (!verifyPermission()) return

        viewModelScope.launch {
            val newTimeout = toggleKeepScreenOnUseCase()
            _selectedOption.value = newTimeout
            val msg = if (newTimeout.milliseconds == TimeoutOption.NEVER.milliseconds)
                "Keep Screen On enabled" else "Restored screen timeout to ${newTimeout.displayLabel}"
            _uiEffect.send(HomeUiEffect.ShowToast(msg))
        }
    }

    /**
     * Restores the previously active screen timeout.
     */
    fun restorePreviousTimeout() {
        if (!verifyPermission()) return

        viewModelScope.launch {
            val restored = timeoutRepository.restorePreviousTimeout()
            if (restored) {
                val newTimeout = timeoutRepository.getCurrentTimeout()
                _selectedOption.value = newTimeout
                _uiEffect.send(HomeUiEffect.ShowToast("Restored previous timeout: ${newTimeout.displayLabel}"))
            } else {
                _uiEffect.send(HomeUiEffect.ShowToast("No previous timeout to restore"))
            }
        }
    }

    /** Triggers the quick settings tile instruction dialog effect. */
    fun onAddTileClicked() {
        viewModelScope.launch { _uiEffect.send(HomeUiEffect.ShowAddTileInstructions) }
    }

    /** Triggers the pin home screen widget request effect. */
    fun onAddWidgetClicked() {
        viewModelScope.launch { _uiEffect.send(HomeUiEffect.RequestAddWidget) }
    }
}
