package com.dhruvanbhalara.dozetap.domain.repository

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import kotlinx.coroutines.flow.Flow

/**
 * Theme configuration options supported by DozeTap.
 */
enum class DarkThemeConfig {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK,
    AMOLED
}

/**
 * Repository interface for managing user preferences and persisted app configuration.
 */
interface IPreferencesRepository {
    /** Observe whether onboarding flow has been completed. */
    fun isOnboardingCompleted(): Flow<Boolean>

    /** Set whether onboarding flow has been completed. */
    suspend fun setOnboardingCompleted(completed: Boolean)
    
    /** Observe the current dark theme mode configuration. */
    fun getDarkThemeConfig(): Flow<DarkThemeConfig>

    /** Update the dark theme mode configuration. */
    suspend fun setDarkThemeConfig(config: DarkThemeConfig)
    
    /** Observe whether dynamic Material You color scheme is enabled. */
    fun isDynamicColorEnabled(): Flow<Boolean>

    /** Update dynamic Material You color scheme preference. */
    suspend fun setDynamicColorEnabled(enabled: Boolean)

    /** Observe default screen timeout preset. */
    fun getDefaultTimeout(): Flow<TimeoutOption>

    /** Update default screen timeout preset. */
    suspend fun setDefaultTimeout(option: TimeoutOption)

    /** Observe recent timeout presets history. */
    fun getRecentTimeouts(): Flow<List<TimeoutOption>>

    /** Add a timeout preset to recent history. */
    suspend fun addRecentTimeout(option: TimeoutOption)

    /** Observe Quick Settings tile toggle cycle order. */
    fun getCycleOrder(): Flow<List<TimeoutOption>>

    /** Update Quick Settings tile toggle cycle order. */
    suspend fun setCycleOrder(options: List<TimeoutOption>)

    /** Observe persisted previous screen timeout for Process Death recovery. */
    fun getPreviousTimeout(): Flow<TimeoutOption?>

    /** Persist previous screen timeout for Process Death recovery. */
    suspend fun setPreviousTimeout(option: TimeoutOption?)

    /** Observe whether text labels are enabled for Quick Settings tile. */
    fun isShowTextLabelsEnabled(): Flow<Boolean>

    /** Update text labels display preference for Quick Settings tile. */
    suspend fun setShowTextLabelsEnabled(enabled: Boolean)

    /** Observe whether haptic feedback vibration is enabled on state change. */
    fun isVibrateOnChangeEnabled(): Flow<Boolean>

    /** Update haptic feedback vibration preference. */
    suspend fun setVibrateOnChangeEnabled(enabled: Boolean)

    /** Observe the user-selected app language BCP-47 tag (e.g., "en", "hi", or "" for system default). */
    fun getAppLanguage(): Flow<String>

    /** Persist the selected app language BCP-47 tag. */
    suspend fun setAppLanguage(languageTag: String)
}
