package com.dhruvanbhalara.dozetap.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import java.io.IOException
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "dozetap_preferences")

/**
 * Implementation of [IPreferencesRepository] utilizing Jetpack DataStore Preferences for persistent storage.
 *
 * @property context The application context used for accessing DataStore.
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IPreferencesRepository {

    private val preferencesFlow = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        val DEFAULT_TIMEOUT_MS = stringPreferencesKey("default_timeout_ms")
        val RECENT_TIMEOUTS = stringPreferencesKey("recent_timeouts")
        val CYCLE_ORDER = stringPreferencesKey("cycle_order")
        val PREVIOUS_TIMEOUT_MS = stringPreferencesKey("previous_timeout_ms")
        val SHOW_TEXT_LABELS = booleanPreferencesKey("show_text_labels")
        val VIBRATE_ON_CHANGE = booleanPreferencesKey("vibrate_on_change")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return preferencesFlow.map { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    override fun getDarkThemeConfig(): Flow<DarkThemeConfig> {
        return preferencesFlow.map { prefs ->
            when (prefs[Keys.DARK_THEME_CONFIG]) {
                DarkThemeConfig.LIGHT.name -> DarkThemeConfig.LIGHT
                DarkThemeConfig.DARK.name -> DarkThemeConfig.DARK
                DarkThemeConfig.AMOLED.name -> DarkThemeConfig.AMOLED
                else -> DarkThemeConfig.FOLLOW_SYSTEM
            }
        }
    }

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME_CONFIG] = config.name
        }
    }

    override fun isDynamicColorEnabled(): Flow<Boolean> {
        return preferencesFlow.map { prefs ->
            prefs[Keys.DYNAMIC_COLOR_ENABLED] ?: true
        }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    override fun getDefaultTimeout(): Flow<TimeoutOption> {
        return preferencesFlow.map { prefs ->
            val msStr = prefs[Keys.DEFAULT_TIMEOUT_MS]
            val ms = msStr?.toIntOrNull() ?: TimeoutOption.THIRTY_SEC.milliseconds
            TimeoutOption.fromMilliseconds(ms)
        }
    }

    override suspend fun setDefaultTimeout(option: TimeoutOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_TIMEOUT_MS] = option.milliseconds.toString()
        }
    }

    override fun getRecentTimeouts(): Flow<List<TimeoutOption>> {
        return preferencesFlow.map { prefs ->
            val raw = prefs[Keys.RECENT_TIMEOUTS]
            if (raw.isNullOrEmpty()) {
                listOf(TimeoutOption.FIFTEEN_SEC, TimeoutOption.TWO_MIN, TimeoutOption.TEN_MIN)
            } else {
                raw.split(",")
                    .mapNotNull { it.toIntOrNull() }
                    .map { TimeoutOption.fromMilliseconds(it) }
            }
        }
    }

    override suspend fun addRecentTimeout(option: TimeoutOption) {
        context.dataStore.edit { prefs ->
            val currentRaw = prefs[Keys.RECENT_TIMEOUTS]
            val currentList = currentRaw?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.map { TimeoutOption.fromMilliseconds(it) }
                ?.toMutableList() ?: mutableListOf(TimeoutOption.FIFTEEN_SEC, TimeoutOption.TWO_MIN, TimeoutOption.TEN_MIN)

            currentList.removeIf { it.milliseconds == option.milliseconds }
            currentList.add(0, option)

            val trimmed = currentList.take(3)
            prefs[Keys.RECENT_TIMEOUTS] = trimmed.joinToString(",") { it.milliseconds.toString() }
        }
    }

    override fun getCycleOrder(): Flow<List<TimeoutOption>> {
        return preferencesFlow.map { prefs ->
            val raw = prefs[Keys.CYCLE_ORDER]
            if (raw.isNullOrEmpty()) {
                TimeoutOption.DEFAULT_PRESETS
            } else {
                raw.split(",")
                    .mapNotNull { it.toIntOrNull() }
                    .map { TimeoutOption.fromMilliseconds(it) }
            }
        }
    }

    override suspend fun setCycleOrder(options: List<TimeoutOption>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CYCLE_ORDER] = options.joinToString(",") { it.milliseconds.toString() }
        }
    }

    override fun getPreviousTimeout(): Flow<TimeoutOption?> {
        return preferencesFlow.map { prefs ->
            val msStr = prefs[Keys.PREVIOUS_TIMEOUT_MS]
            msStr?.toIntOrNull()?.let { TimeoutOption.fromMilliseconds(it) }
        }
    }

    override suspend fun setPreviousTimeout(option: TimeoutOption?) {
        context.dataStore.edit { prefs ->
            if (option == null) {
                prefs.remove(Keys.PREVIOUS_TIMEOUT_MS)
            } else {
                prefs[Keys.PREVIOUS_TIMEOUT_MS] = option.milliseconds.toString()
            }
        }
    }

    override fun isShowTextLabelsEnabled(): Flow<Boolean> {
        return preferencesFlow.map { prefs ->
            prefs[Keys.SHOW_TEXT_LABELS] ?: true
        }
    }

    override suspend fun setShowTextLabelsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_TEXT_LABELS] = enabled
        }
    }

    override fun isVibrateOnChangeEnabled(): Flow<Boolean> {
        return preferencesFlow.map { prefs ->
            prefs[Keys.VIBRATE_ON_CHANGE] ?: true
        }
    }

    override suspend fun setVibrateOnChangeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VIBRATE_ON_CHANGE] = enabled
        }
    }

    override fun getAppLanguage(): Flow<String> {
        return preferencesFlow.map { prefs ->
            prefs[Keys.APP_LANGUAGE] ?: ""
        }
    }

    override suspend fun setAppLanguage(languageTag: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = languageTag
        }
    }
}
