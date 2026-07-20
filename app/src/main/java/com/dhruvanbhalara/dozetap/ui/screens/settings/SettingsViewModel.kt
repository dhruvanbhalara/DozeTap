package com.dhruvanbhalara.dozetap.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Immutable
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.util.PlatformSystemManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the Settings screen.
 *
 * @property defaultTimeout Default screen timeout preset.
 * @property darkThemeConfig Current dark theme configuration.
 * @property isDynamicColorEnabled Whether dynamic Material You color palette is active.
 * @property cycleOrder Ordered list of presets for Quick Settings tile cycle.
 * @property showTextLabels Whether text labels are displayed in the Quick Settings tile.
 * @property vibrateOnChange Whether haptic feedback is triggered on tile state changes.
 * @property appVersion Installed app version string.
 * @property appLanguage BCP-47 language tag of the currently selected in-app language (e.g., "en", "hi", or "" for system default).
 */
@Immutable
data class SettingsUiState(
    val defaultTimeout: TimeoutOption = TimeoutOption.THIRTY_SEC,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val isDynamicColorEnabled: Boolean = true,
    val cycleOrder: List<TimeoutOption> = TimeoutOption.DEFAULT_PRESETS,
    val showTextLabels: Boolean = true,
    val vibrateOnChange: Boolean = true,
    val appVersion: String = "1.0.0",
    val appLanguage: String = ""
)

/**
 * ViewModel managing app settings and user preferences.
 *
 * @property preferencesRepository DataStore repository for persisting application settings.
 * @property platformSystemManager Framework system service helper.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: IPreferencesRepository,
    val platformSystemManager: PlatformSystemManager
) : ViewModel() {

    /** Triggers native Quick Settings tile addition system prompt. */
    fun requestAddTile(): Boolean = platformSystemManager.requestAddTileNative()

    /** Reactive stream of [SettingsUiState]. */
    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.getDefaultTimeout(),
        preferencesRepository.getDarkThemeConfig(),
        preferencesRepository.isDynamicColorEnabled(),
        preferencesRepository.getCycleOrder(),
        combine(
            preferencesRepository.isShowTextLabelsEnabled(),
            preferencesRepository.isVibrateOnChangeEnabled(),
            preferencesRepository.getAppLanguage(),
            ::Triple
        )
    ) { defaultTimeout, darkTheme, dynamicColor, cycleOrder, (showLabels, vibrate, lang) ->
        SettingsUiState(
            defaultTimeout = defaultTimeout,
            darkThemeConfig = darkTheme,
            isDynamicColorEnabled = dynamicColor,
            cycleOrder = cycleOrder,
            showTextLabels = showLabels,
            vibrateOnChange = vibrate,
            appLanguage = lang
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    /** Updates default timeout preset preference. */
    fun setDefaultTimeout(option: TimeoutOption) {
        viewModelScope.launch {
            preferencesRepository.setDefaultTimeout(option)
        }
    }

    /** Updates dark theme configuration. */
    fun setDarkThemeConfig(config: DarkThemeConfig) {
        viewModelScope.launch {
            preferencesRepository.setDarkThemeConfig(config)
        }
    }

    /** Updates dynamic color palette preference. */
    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDynamicColorEnabled(enabled)
        }
    }

    /** Updates Quick Settings tile toggle cycle order. */
    fun setCycleOrder(options: List<TimeoutOption>) {
        viewModelScope.launch {
            preferencesRepository.setCycleOrder(options)
        }
    }

    /** Updates Quick Settings tile text label display preference. */
    fun setShowTextLabelsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowTextLabelsEnabled(enabled)
        }
    }

    /** Updates haptic feedback vibration preference. */
    fun setVibrateOnChangeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setVibrateOnChangeEnabled(enabled)
        }
    }

    /**
     * Persists and immediately applies the selected language.
     *
     * @param languageTag BCP-47 tag (e.g., "en", "hi") or empty string to restore system default.
     */
    fun setAppLanguage(languageTag: String) {
        viewModelScope.launch {
            preferencesRepository.setAppLanguage(languageTag)
            val locale = if (languageTag.isEmpty()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageTag)
            }
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }
}
