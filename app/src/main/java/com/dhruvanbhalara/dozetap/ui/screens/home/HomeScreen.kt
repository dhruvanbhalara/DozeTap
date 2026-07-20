package com.dhruvanbhalara.dozetap.ui.screens.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.key
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhruvanbhalara.dozetap.R
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.ui.components.PermissionBanner
import com.dhruvanbhalara.dozetap.ui.util.localizedShortLabel

/**
 * Main Dashboard screen displaying current screen timeout status, quick presets grid,
 * keep awake toggle action, and compact quick setup CTAs.
 *
 * @param viewModel ViewModel managing home dashboard state and side effects.
 * @param onNavigateToSettings Callback to navigate to settings screen.
 * @param modifier Root layout modifier.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showTileDialog by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        viewModel.refreshState()
        onPauseOrDispose { }
    }

    val widgetHintMessage = stringResource(R.string.home_toast_widget_hint)

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is HomeUiEffect.RequestWriteSettingsPermission -> {
                    context.startActivity(viewModel.platformSystemManager.getWriteSettingsPermissionIntent())
                }
                is HomeUiEffect.ShowAddTileInstructions -> {
                    if (!viewModel.platformSystemManager.requestAddTileNative()) {
                        showTileDialog = true
                    }
                }
                is HomeUiEffect.RequestAddWidget -> {
                    if (!viewModel.platformSystemManager.requestAddWidget()) {
                        Toast.makeText(context, widgetHintMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val heroContainerColor by animateColorAsState(
        targetValue = if (uiState.isKeepScreenOnActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "heroContainerColor"
    )

    val heroContentColor by animateColorAsState(
        targetValue = if (uiState.isKeepScreenOnActive) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "heroContentColor"
    )

    val keepAwakeRotation by animateFloatAsState(
        targetValue = if (uiState.isKeepScreenOnActive) 360f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "keepAwakeRotation"
    )

    val heroCircleScale by animateFloatAsState(
        targetValue = if (uiState.isKeepScreenOnActive) 1.06f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heroCircleScale"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.nav_settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
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
            Spacer(modifier = Modifier.height(4.dp))

            if (!uiState.canWriteSettings) {
                PermissionBanner(
                    onRequestPermission = {
                        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    },
                    isShizukuRunning = uiState.isShizukuRunning,
                    onRequestShizukuPermission = {
                        viewModel.grantPermissionWithShizuku()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val actionAlpha = if (uiState.canWriteSettings) 1.0f else 0.6f

            // 1. HERO STATUS HEADER (Restored Active Timeout Visualizer)
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = heroContainerColor,
                contentColor = heroContentColor,
                tonalElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = actionAlpha }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.home_current_timeout_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = heroContentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(112.dp)
                            .graphicsLayer {
                                scaleX = heroCircleScale
                                scaleY = heroCircleScale
                            },
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedContent(
                                    targetState = if (uiState.isKeepScreenOnActive) {
                                        stringResource(R.string.home_status_awake)
                                    } else {
                                        uiState.currentSystemTimeout.localizedShortLabel()
                                    },
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "timeoutDisplay"
                                ) { label ->
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = if (uiState.isKeepScreenOnActive) {
                                        stringResource(R.string.home_status_awake)
                                    } else {
                                        stringResource(R.string.home_status_timeout)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleKeepScreenOn()
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isKeepScreenOnActive) Icons.Rounded.CheckCircle else Icons.Rounded.Nightlight,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = keepAwakeRotation }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isKeepScreenOnActive) {
                                stringResource(R.string.home_btn_disable_keep_awake)
                            } else {
                                stringResource(R.string.home_btn_keep_awake)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. QUICK PRESETS GRID (Symmetrical 4 Columns x 2 Rows)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.home_section_quick_presets),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = actionAlpha },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                val presets = remember { TimeoutOption.DEFAULT_PRESETS }
                presets.forEach { option ->
                    key(option.milliseconds) {
                        val isSelected = uiState.selectedOption.milliseconds == option.milliseconds && !uiState.isKeepScreenOnActive
                        val isCurrentApplied = uiState.currentSystemTimeout.milliseconds == option.milliseconds && !uiState.isKeepScreenOnActive

                        val cardContainerColor = when {
                            isSelected || isCurrentApplied -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceContainer
                        }

                        val cardContentColor = when {
                            isSelected || isCurrentApplied -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        val cardBorder = if (isSelected || isCurrentApplied) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        }

                        val isNever = option.milliseconds == Int.MAX_VALUE
                        val cardIcon = when {
                            isSelected || isCurrentApplied -> Icons.Rounded.CheckCircle
                            isNever -> Icons.Rounded.AllInclusive
                            else -> Icons.Rounded.Timer
                        }

                        val iconTint = when {
                            isSelected || isCurrentApplied -> cardContentColor
                            isNever -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }

                        val textColor = when {
                            isNever -> MaterialTheme.colorScheme.error
                            isSelected || isCurrentApplied -> cardContentColor
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        val localizedLabel = option.localizedShortLabel()
                        val cardInteractionSource = remember { MutableInteractionSource() }
                        val isCardPressed by cardInteractionSource.collectIsPressedAsState()
                        val cardScale by animateFloatAsState(
                            targetValue = if (isCardPressed) 0.94f else 1.0f,
                            animationSpec = spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "cardScale"
                        )

                        Card(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectTimeoutOption(option)
                                viewModel.applyTimeout()
                            },
                            interactionSource = cardInteractionSource,
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = cardContainerColor,
                                contentColor = cardContentColor
                            ),
                            border = cardBorder,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .graphicsLayer {
                                    scaleX = cardScale
                                    scaleY = cardScale
                                }
                                .semantics(mergeDescendants = true) {}
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = cardIcon,
                                    contentDescription = localizedLabel,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = localizedLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. QUICK ACCESS SETUP CARD (Positioned Below Presets)
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.home_quick_setup_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (!viewModel.platformSystemManager.requestAddTileNative()) {
                                    showTileDialog = true
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.home_btn_add_qs_tile),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (!viewModel.platformSystemManager.requestAddWidget()) {
                                    Toast.makeText(context, widgetHintMessage, Toast.LENGTH_LONG).show()
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Widgets,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.home_btn_add_widget),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showTileDialog) {
        AlertDialog(
            onDismissRequest = { showTileDialog = false },
            title = { Text(text = stringResource(R.string.home_dialog_add_tile_title), style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(text = stringResource(R.string.home_dialog_add_tile_body), style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { showTileDialog = false }) {
                    Text(stringResource(R.string.btn_got_it), style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}
