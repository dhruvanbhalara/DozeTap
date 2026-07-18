package com.dhruvanbhalara.dozetap.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom semantic status tokens for DozeTap.
 * Complements standard Material 3 [androidx.compose.material3.ColorScheme] by providing
 * domain-specific operational state colors (success, warning, info, keep-screen-on active).
 */
@Immutable
data class DozeTapSemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val activeKeepScreenOn: Color,
    val onActiveKeepScreenOn: Color,
    val activeKeepScreenOnContainer: Color
)

/** Light theme semantic colors instance. */
val LightSemanticColors = DozeTapSemanticColors(
    success = LightSuccess,
    onSuccess = LightOnSuccess,
    successContainer = LightSuccessContainer,
    warning = LightWarning,
    onWarning = LightOnWarning,
    warningContainer = LightWarningContainer,
    info = LightInfo,
    onInfo = LightOnInfo,
    infoContainer = LightInfoContainer,
    activeKeepScreenOn = LightTertiary,
    onActiveKeepScreenOn = LightOnTertiary,
    activeKeepScreenOnContainer = LightTertiaryContainer
)

/** Dark theme semantic colors instance. */
val DarkSemanticColors = DozeTapSemanticColors(
    success = DarkSuccess,
    onSuccess = DarkOnSuccess,
    successContainer = DarkSuccessContainer,
    warning = DarkWarning,
    onWarning = DarkOnWarning,
    warningContainer = DarkWarningContainer,
    info = DarkInfo,
    onInfo = DarkOnInfo,
    infoContainer = DarkInfoContainer,
    activeKeepScreenOn = DarkTertiary,
    onActiveKeepScreenOn = DarkOnTertiary,
    activeKeepScreenOnContainer = DarkTertiaryContainer
)

/** CompositionLocal key for accessing [DozeTapSemanticColors] throughout the UI hierarchy. */
val LocalDozeTapSemanticColors: ProvidableCompositionLocal<DozeTapSemanticColors> =
    staticCompositionLocalOf { LightSemanticColors }
