package com.dhruvanbhalara.dozetap.fakes

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake implementation of [IPreferencesRepository] for unit testing.
 */
class FakePreferencesRepository : IPreferencesRepository {

    private val onboardingFlow = MutableStateFlow(false)
    private val darkThemeFlow = MutableStateFlow(DarkThemeConfig.FOLLOW_SYSTEM)
    private val dynamicColorFlow = MutableStateFlow(true)
    private val defaultTimeoutFlow = MutableStateFlow(TimeoutOption.THIRTY_SEC)
    private val recentTimeoutsFlow = MutableStateFlow(
        listOf(TimeoutOption.FIFTEEN_SEC, TimeoutOption.TWO_MIN, TimeoutOption.TEN_MIN)
    )
    private val cycleOrderFlow = MutableStateFlow(TimeoutOption.DEFAULT_PRESETS)
    private val previousTimeoutFlow = MutableStateFlow<TimeoutOption?>(null)
    private val showTextLabelsFlow = MutableStateFlow(true)
    private val vibrateOnChangeFlow = MutableStateFlow(true)
    private val appLanguageFlow = MutableStateFlow("")

    override fun isOnboardingCompleted(): Flow<Boolean> = onboardingFlow

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingFlow.value = completed
    }

    override fun getDarkThemeConfig(): Flow<DarkThemeConfig> = darkThemeFlow

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        darkThemeFlow.value = config
    }

    override fun isDynamicColorEnabled(): Flow<Boolean> = dynamicColorFlow

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dynamicColorFlow.value = enabled
    }

    override fun getDefaultTimeout(): Flow<TimeoutOption> = defaultTimeoutFlow

    override suspend fun setDefaultTimeout(option: TimeoutOption) {
        defaultTimeoutFlow.value = option
    }

    override fun getRecentTimeouts(): Flow<List<TimeoutOption>> = recentTimeoutsFlow

    override suspend fun addRecentTimeout(option: TimeoutOption) {
        val list = recentTimeoutsFlow.value.toMutableList()
        list.removeIf { it.milliseconds == option.milliseconds }
        list.add(0, option)
        recentTimeoutsFlow.value = list.take(3)
    }

    override fun getCycleOrder(): Flow<List<TimeoutOption>> = cycleOrderFlow

    override suspend fun setCycleOrder(options: List<TimeoutOption>) {
        cycleOrderFlow.value = options
    }

    override fun getPreviousTimeout(): Flow<TimeoutOption?> = previousTimeoutFlow

    override suspend fun setPreviousTimeout(option: TimeoutOption?) {
        previousTimeoutFlow.value = option
    }

    override fun isShowTextLabelsEnabled(): Flow<Boolean> = showTextLabelsFlow

    override suspend fun setShowTextLabelsEnabled(enabled: Boolean) {
        showTextLabelsFlow.value = enabled
    }

    override fun isVibrateOnChangeEnabled(): Flow<Boolean> = vibrateOnChangeFlow

    override suspend fun setVibrateOnChangeEnabled(enabled: Boolean) {
        vibrateOnChangeFlow.value = enabled
    }

    override fun getAppLanguage(): Flow<String> = appLanguageFlow

    override suspend fun setAppLanguage(languageTag: String) {
        appLanguageFlow.value = languageTag
    }
}
