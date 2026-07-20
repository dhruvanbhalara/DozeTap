package com.dhruvanbhalara.dozetap

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.ui.components.DozeTapBackground
import com.dhruvanbhalara.dozetap.ui.screens.home.HomeScreen
import com.dhruvanbhalara.dozetap.ui.screens.home.HomeViewModel
import com.dhruvanbhalara.dozetap.ui.screens.settings.SettingsScreen
import com.dhruvanbhalara.dozetap.ui.screens.settings.SettingsViewModel
import com.dhruvanbhalara.dozetap.ui.theme.DozeTapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * Top-level route destination constants for DozeTap.
 */
object DozeTapDestinations {
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
}

private class LocalizedActivityContext(
    base: android.content.Context,
    private val localizedContext: android.content.Context
) : android.content.ContextWrapper(base) {
    override fun getResources(): android.content.res.Resources = localizedContext.resources
    override fun getAssets(): android.content.res.AssetManager = localizedContext.assets
}

/**
 * Main Activity host for DozeTap.
 * Configured with [AndroidEntryPoint] for Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepository: IPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Asynchronously restore persisted language on cold start without blocking main UI looper
        lifecycleScope.launch {
            val savedTag = preferencesRepository.getAppLanguage().first()
            if (savedTag.isNotEmpty()) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedTag))
            }
        }

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            val context = LocalContext.current
            val localizedContext = remember(context, settingsState.appLanguage) {
                if (settingsState.appLanguage.isEmpty()) {
                    context
                } else {
                    val locale = Locale.forLanguageTag(settingsState.appLanguage)
                    val config = Configuration(context.resources.configuration)
                    config.setLocale(locale)
                    config.setLayoutDirection(locale)
                    val configContext = context.createConfigurationContext(config)
                    LocalizedActivityContext(context, configContext)
                }
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                DozeTapTheme(
                    darkThemeConfig = settingsState.darkThemeConfig,
                    dynamicColor = settingsState.isDynamicColorEnabled
                ) {
                    DozeTapBackground {
                        DozeTapAppNavigation(settingsViewModel = settingsViewModel)
                    }
                }
            }
        }
    }
}

/**
 * Single-Screen application navigation structure hosting Dashboard and Settings.
 * Zero bottom bar navigation for fast 1-tap utility access.
 *
 * @param settingsViewModel Shared [SettingsViewModel] instance.
 */
@Composable
fun DozeTapAppNavigation(
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()

    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = DozeTapDestinations.DASHBOARD,
            enterTransition = { slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut() }
        ) {
            composable(DozeTapDestinations.DASHBOARD) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSettings = {
                        navController.navigate(DozeTapDestinations.SETTINGS)
                    }
                )
            }
            composable(DozeTapDestinations.SETTINGS) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
