package com.dhruvanbhalara.dozetap.ui.screens.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dhruvanbhalara.dozetap.fakes.FakePreferencesRepository
import com.dhruvanbhalara.dozetap.ui.theme.DozeTapTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation UI component tests for [SettingsScreen].
 */
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_rendersTitleAndSections() {
        val prefsRepo = FakePreferencesRepository()
        val viewModel = SettingsViewModel(prefsRepo)

        composeTestRule.setContent {
            DozeTapTheme {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("General").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quick Settings Tile").assertIsDisplayed()
    }
}
