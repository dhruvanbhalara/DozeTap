package com.dhruvanbhalara.dozetap.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhruvanbhalara.dozetap.R
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.repository.DarkThemeConfig
import com.dhruvanbhalara.dozetap.ui.util.localizedDisplayLabel

/** Maps [DarkThemeConfig] to its localized string resource ID. Lives in the UI layer to keep domain clean. */
@StringRes
private fun DarkThemeConfig.labelRes(): Int = when (this) {
    DarkThemeConfig.FOLLOW_SYSTEM -> R.string.settings_theme_follow_system
    DarkThemeConfig.LIGHT -> R.string.settings_theme_light
    DarkThemeConfig.DARK -> R.string.settings_theme_dark
    DarkThemeConfig.AMOLED -> R.string.settings_theme_amoled
}

/**
 * Settings screen displaying app preferences, Quick Settings tile toggles, theme customization,
 * language selection, and system about disclosures.
 *
 * @param viewModel ViewModel managing application settings.
 * @param onNavigateBack Callback for navigating back to the home screen.
 * @param modifier Root layout modifier.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDefaultTimeoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showShizukuDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val currentLanguageLabel = when (uiState.appLanguage) {
        "hi" -> stringResource(R.string.lang_hindi)
        else -> stringResource(R.string.lang_english)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.btn_close),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_general))
            SettingsGroupContainer {
                SettingsClickableItem(
                    icon = Icons.Rounded.Timer,
                    title = stringResource(R.string.settings_item_default_timeout),
                    subtitle = uiState.defaultTimeout.localizedDisplayLabel(),
                    onClick = { showDefaultTimeoutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_tile))
            SettingsGroupContainer {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Subtitles,
                    title = stringResource(R.string.tiles_show_labels_title),
                    subtitle = stringResource(R.string.tiles_show_labels_sub),
                    checked = uiState.showTextLabels,
                    onCheckedChange = { viewModel.setShowTextLabelsEnabled(it) }
                )
                SettingsSwitchItem(
                    icon = Icons.Rounded.Vibration,
                    title = stringResource(R.string.tiles_vibrate_title),
                    subtitle = stringResource(R.string.tiles_vibrate_sub),
                    checked = uiState.vibrateOnChange,
                    onCheckedChange = { viewModel.setVibrateOnChangeEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_language))
            SettingsGroupContainer {
                SettingsClickableItem(
                    icon = Icons.Rounded.Language,
                    title = stringResource(R.string.settings_item_language),
                    subtitle = currentLanguageLabel,
                    onClick = { showLanguageDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_appearance))
            SettingsGroupContainer {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Palette,
                    title = stringResource(R.string.settings_item_dynamic_color),
                    subtitle = stringResource(R.string.settings_item_dynamic_color_sub),
                    checked = uiState.isDynamicColorEnabled,
                    onCheckedChange = { viewModel.setDynamicColorEnabled(it) }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.DarkMode,
                    title = stringResource(R.string.settings_item_dark_mode),
                    subtitle = stringResource(uiState.darkThemeConfig.labelRes()),
                    onClick = { showThemeDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_system_about))
            SettingsGroupContainer {
                SettingsClickableItem(
                    icon = Icons.Rounded.BatteryFull,
                    title = stringResource(R.string.settings_item_battery_opt),
                    subtitle = stringResource(R.string.settings_item_battery_opt_sub),
                    onClick = {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.Bolt,
                    title = stringResource(R.string.settings_item_shizuku),
                    subtitle = stringResource(R.string.settings_item_shizuku_sub),
                    onClick = { showShizukuDialog = true }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.PrivacyTip,
                    title = stringResource(R.string.settings_item_privacy),
                    subtitle = stringResource(R.string.settings_item_privacy_sub),
                    onClick = { showPrivacyDialog = true }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.Info,
                    title = stringResource(R.string.settings_item_about, com.dhruvanbhalara.dozetap.util.AppConstants.APP_NAME),
                    subtitle = stringResource(R.string.settings_item_about_sub, com.dhruvanbhalara.dozetap.BuildConfig.VERSION_NAME),
                    onClick = { }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.Feedback,
                    title = stringResource(R.string.settings_item_feedback),
                    subtitle = stringResource(R.string.settings_item_feedback_sub),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${com.dhruvanbhalara.dozetap.util.AppConstants.SUPPORT_EMAIL}?subject=${com.dhruvanbhalara.dozetap.util.AppConstants.FEEDBACK_SUBJECT}")
                        }
                        context.startActivity(intent)
                    }
                )
                SettingsClickableItem(
                    icon = Icons.Rounded.Code,
                    title = stringResource(R.string.settings_item_github),
                    subtitle = stringResource(R.string.settings_item_github_sub),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(com.dhruvanbhalara.dozetap.util.AppConstants.GITHUB_REPO_URL))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showDefaultTimeoutDialog) {
        ModalBottomSheet(
            onDismissRequest = { showDefaultTimeoutDialog = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_dialog_select_timeout),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                TimeoutOption.DEFAULT_PRESETS.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.setDefaultTimeout(option)
                                showDefaultTimeoutDialog = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        RadioButton(
                            selected = uiState.defaultTimeout.milliseconds == option.milliseconds,
                            onClick = {
                                viewModel.setDefaultTimeout(option)
                                showDefaultTimeoutDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = option.localizedDisplayLabel(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showThemeDialog) {
        ModalBottomSheet(
            onDismissRequest = { showThemeDialog = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_dialog_select_theme),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                val themeOptions = listOf(
                    DarkThemeConfig.FOLLOW_SYSTEM,
                    DarkThemeConfig.LIGHT,
                    DarkThemeConfig.DARK,
                    DarkThemeConfig.AMOLED
                )
                themeOptions.forEach { config ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.setDarkThemeConfig(config)
                                showThemeDialog = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        RadioButton(
                            selected = uiState.darkThemeConfig == config,
                            onClick = {
                                viewModel.setDarkThemeConfig(config)
                                showThemeDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(config.labelRes()),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showLanguageDialog) {
        val languages = listOf(
            "en" to stringResource(R.string.lang_english),
            "hi" to stringResource(R.string.lang_hindi)
        )
        ModalBottomSheet(
            onDismissRequest = { showLanguageDialog = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_dialog_select_language),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                languages.forEach { (tag, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.setAppLanguage(tag)
                                showLanguageDialog = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        RadioButton(
                            selected = uiState.appLanguage == tag,
                            onClick = {
                                viewModel.setAppLanguage(tag)
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.settings_item_privacy), style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(text = com.dhruvanbhalara.dozetap.util.AppConstants.PRIVACY_POLICY_TEXT, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(stringResource(R.string.btn_got_it), style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }

    if (showShizukuDialog) {
        AlertDialog(
            onDismissRequest = { showShizukuDialog = false },
            title = { Text(stringResource(R.string.settings_dialog_shizuku_title), style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.settings_dialog_shizuku_body),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.settings_dialog_shizuku_setup),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showShizukuDialog = false }) {
                    Text(stringResource(R.string.btn_got_it), style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsGroupContainer(content: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "itemScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(MaterialTheme.shapes.large)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
