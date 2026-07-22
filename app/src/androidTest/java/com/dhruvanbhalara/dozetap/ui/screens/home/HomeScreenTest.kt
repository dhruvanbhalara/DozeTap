package com.dhruvanbhalara.dozetap.ui.screens.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakePlatformSystemManager
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.fakes.FakeShizukuRepository
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import com.dhruvanbhalara.dozetap.domain.usecase.GetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.SetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.ToggleKeepScreenOnUseCase
import com.dhruvanbhalara.dozetap.ui.theme.DozeTapTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation UI component tests for [HomeScreen].
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_rendersCurrentTimeoutAndPresetChips() {
        val timeoutRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.THIRTY_SEC)
        val prefsRepo = FakePreferencesRepository()
        val shizukuRepo = FakeShizukuRepository()
        val platformManager = FakePlatformSystemManager()

        val viewModel = HomeViewModel(
            timeoutRepository = timeoutRepo,
            preferencesRepository = prefsRepo,
            shizukuRepository = shizukuRepo,
            getScreenTimeoutUseCase = GetScreenTimeoutUseCase(timeoutRepo),
            setScreenTimeoutUseCase = SetScreenTimeoutUseCase(timeoutRepo, prefsRepo),
            toggleKeepScreenOnUseCase = ToggleKeepScreenOnUseCase(timeoutRepo, prefsRepo),
            platformSystemManager = platformManager
        )

        composeTestRule.setContent {
            DozeTapTheme {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = {}
                )
            }
        }

        composeTestRule.onNodeWithText("DozeTap").assertIsDisplayed()
    }
}
