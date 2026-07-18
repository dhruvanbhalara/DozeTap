package com.dhruvanbhalara.dozetap.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Emerald Aurora Color Palette for DozeTap.
 * Grounded in WCAG 2.1 AA accessibility guidelines, Material Design 3 semantic color roles,
 * and high-contrast dark/AMOLED ergonomics with clear visual surface separation.
 */

// ============================================================================
// 1. Light Theme Tokens (Crisp Emerald Slate)
// ============================================================================

/** Primary brand color for high-emphasis buttons and active controls in Light mode. */
val LightPrimary = Color(0xFF059669)

/** Text and icon color displayed on top of [LightPrimary]. */
val LightOnPrimary = Color(0xFFFFFFFF)

/** Soft mint container background for active cards and selected chips in Light mode. */
val LightPrimaryContainer = Color(0xFFD1FAE5)

/** Dark forest text and icon color displayed on top of [LightPrimaryContainer]. */
val LightOnPrimaryContainer = Color(0xFF064E3B)

/** Primary action color used within inverse surfaces in Light mode. */
val LightInversePrimary = Color(0xFF34D399)

/** Secondary accent color for secondary actions and tab indicators in Light mode. */
val LightSecondary = Color(0xFF10B981)

/** Text and icon color displayed on top of [LightSecondary]. */
val LightOnSecondary = Color(0xFFFFFFFF)

/** Mint background for secondary selected states in Light mode. */
val LightSecondaryContainer = Color(0xFFE6F4EA)

/** Deep emerald text and icon color displayed on top of [LightSecondaryContainer]. */
val LightOnSecondaryContainer = Color(0xFF046C4E)

/** Deep cyan teal tertiary color for status highlights and keep-awake banners in Light mode. */
val LightTertiary = Color(0xFF0D9488)

/** Text and icon color displayed on top of [LightTertiary]. */
val LightOnTertiary = Color(0xFFFFFFFF)

/** Soft cyan container background in Light mode. */
val LightTertiaryContainer = Color(0xFFCCFBF1)

/** Text and icon color displayed on top of [LightTertiaryContainer]. */
val LightOnTertiaryContainer = Color(0xFF134E4A)

/** Crisp cool slate background color for Light mode root window. */
val LightBackground = Color(0xFFF8FAFC)

/** Body text and icon color displayed on top of [LightBackground]. */
val LightOnBackground = Color(0xFF0F172A)

/** Pure crisp white card surface for Light mode. */
val LightSurface = Color(0xFFFFFFFF)

/** High-contrast dark slate body text displayed on top of [LightSurface]. */
val LightOnSurface = Color(0xFF0F172A)

/** Dimmed surface state for Light mode. */
val LightSurfaceDim = Color(0xFFE2E8F0)

/** Elevated bright surface state for Light mode. */
val LightSurfaceBright = Color(0xFFF8FAFC)

/** Lowest elevation container fill in Light mode. */
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)

/** Low elevation container fill in Light mode. */
val LightSurfaceContainerLow = Color(0xFFF1F5F9)

/** Default container fill for cards and list sections in Light mode (Pure Crisp White). */
val LightSurfaceContainer = Color(0xFFFFFFFF)

/** High elevation container fill for dialogs and bottom sheets in Light mode. */
val LightSurfaceContainerHigh = Color(0xFFF1F5F9)

/** Highest elevation container fill in Light mode. */
val LightSurfaceContainerHighest = Color(0xFFE2E8F0)

/** Distinct container background for search bars and text inputs in Light mode. */
val LightSurfaceVariant = Color(0xFFF1F5F9)

/** Refined slate gray secondary text and inactive icon color (7.0:1 WCAG contrast). */
val LightOnSurfaceVariant = Color(0xFF64748B)

/** Dark inverse surface color for snackbars in Light mode. */
val LightInverseSurface = Color(0xFF1E293B)

/** Light text color displayed on top of [LightInverseSurface]. */
val LightInverseOnSurface = Color(0xFFF8FAFC)

/** Border outline color for focused inputs and card borders in Light mode. */
val LightOutline = Color(0xFFCBD5E1)

/** Subtle outline color for list dividers and card border strokes in Light mode. */
val LightOutlineVariant = Color(0xFFE2E8F0)

/** High-contrast red error color for dangerous actions and alert states in Light mode. */
val LightError = Color(0xFFDC2626)

/** Text and icon color displayed on top of [LightError]. */
val LightOnError = Color(0xFFFFFFFF)

/** Light red error alert banner container background in Light mode. */
val LightErrorContainer = Color(0xFFFEE2E2)

/** Error text color displayed inside [LightErrorContainer]. */
val LightOnErrorContainer = Color(0xFF7F1D1D)

// ============================================================================
// 2. Dark Theme Tokens (Emerald Aurora - Cyber Teal & Slate Green)
// ============================================================================

/** Deep obsidian teal root window background for Dark mode. */
val DarkBackground = Color(0xFF0B1512)

/** Soft mint-white body text color displayed on top of [DarkBackground]. */
val DarkOnBackground = Color(0xFFF0FDF4)

/** Luminous emerald mint primary color for key actions in Dark mode. */
val DarkPrimary = Color(0xFF34D399)

/** Dark obsidian green text color displayed on top of [DarkPrimary]. */
val DarkOnPrimary = Color(0xFF062016)

/** Deep forest teal container background for Dark mode active states. */
val DarkPrimaryContainer = Color(0xFF064E3B)

/** High-contrast mint text color displayed on top of [DarkPrimaryContainer]. */
val DarkOnPrimaryContainer = Color(0xFFD1FAE5)

/** Light primary action color used inside inverse surfaces in Dark mode. */
val DarkInversePrimary = Color(0xFF059669)

/** Emerald teal secondary color for secondary action highlights in Dark mode. */
val DarkSecondary = Color(0xFF10B981)

/** Deep teal text color displayed on top of [DarkSecondary]. */
val DarkOnSecondary = Color(0xFF022C22)

/** Deep emerald secondary container fill in Dark mode. */
val DarkSecondaryContainer = Color(0xFF0E4D3A)

/** High-contrast mint text displayed on top of [DarkSecondaryContainer]. */
val DarkOnSecondaryContainer = Color(0xFFE6F4EA)

/** Soft neon cyan-jade tertiary color for active keep-awake highlights in Dark mode. */
val DarkTertiary = Color(0xFF6EE7B7)

/** Deep teal text color displayed on top of [DarkTertiary]. */
val DarkOnTertiary = Color(0xFF022C22)

/** Dark cyan container fill in Dark mode. */
val DarkTertiaryContainer = Color(0xFF134E4A)

/** Light cyan text displayed on top of [DarkTertiaryContainer]. */
val DarkOnTertiaryContainer = Color(0xFFCCFBF1)

/** Base slate teal surface in Dark mode. */
val DarkSurface = Color(0xFF142420)

/** Soft high-contrast mint body text displayed on top of [DarkSurface]. */
val DarkOnSurface = Color(0xFFF0FDF4)

/** Dimmed surface state for Dark mode. */
val DarkSurfaceDim = Color(0xFF0E1A17)

/** Slightly brighter surface state for Dark mode. */
val DarkSurfaceBright = Color(0xFF1D332D)

/** Lowest elevation container fill in Dark mode. */
val DarkSurfaceContainerLowest = Color(0xFF0B1512)

/** Low elevation container fill in Dark mode. */
val DarkSurfaceContainerLow = Color(0xFF0E1A17)

/** Card container fill in Dark mode (Distinct Slate Teal - 2-tone contrast against #0B1512). */
val DarkSurfaceContainer = Color(0xFF142420)

/** High elevation container fill for dialogs and bottom sheets in Dark mode. */
val DarkSurfaceContainerHigh = Color(0xFF1D332D)

/** Highest elevation container fill in Dark mode. */
val DarkSurfaceContainerHighest = Color(0xFF264239)

/** Search input and text field container surface in Dark mode. */
val DarkSurfaceVariant = Color(0xFF192C27)

/** Refined cool slate gray secondary text and inactive icon color (Calm & Readable). */
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

/** Bright inverse surface color for dark mode snackbars. */
val DarkInverseSurface = Color(0xFFF0FDF4)

/** Dark text color displayed on top of [DarkInverseSurface]. */
val DarkInverseOnSurface = Color(0xFF0B1512)

/** Border outline color for focused inputs and card borders in Dark mode. */
val DarkOutline = Color(0xFF2D4D44)

/** Subtle outline color for list dividers and card border strokes in Dark mode. */
val DarkOutlineVariant = Color(0xFF1F3831)

/** High-visibility soft red error color in Dark mode. */
val DarkError = Color(0xFFF87171)

/** Dark red text color displayed on top of [DarkError]. */
val DarkOnError = Color(0xFF450A0A)

/** Muted dark red error container fill in Dark mode. */
val DarkErrorContainer = Color(0xFF7F1D1D)

/** Light red error text color displayed inside [DarkErrorContainer]. */
val DarkOnErrorContainer = Color(0xFFFEE2E2)

// ============================================================================
// 3. Pure AMOLED Black Tokens (Pitch Black #000000 with Near-Black Emerald Surfaces)
// ============================================================================

/** Pure pitch black background (#000000) for OLED battery power optimization. */
val AmoledBackground = Color(0xFF000000)

/** Near-black dark emerald surface for cards in AMOLED mode. */
val AmoledSurface = Color(0xFF101E1A)

/** Dimmed surface background in AMOLED mode. */
val AmoledSurfaceDim = Color(0xFF091210)

/** Slightly brighter surface in AMOLED mode. */
val AmoledSurfaceBright = Color(0xFF182B25)

/** Lowest elevation container fill in AMOLED mode (Pure Pitch Black). */
val AmoledSurfaceContainerLowest = Color(0xFF000000)

/** Low elevation container fill in AMOLED mode. */
val AmoledSurfaceContainerLow = Color(0xFF091210)

/** Card container fill in AMOLED mode (Near-black Emerald - 2-tone card on #000000 black). */
val AmoledSurfaceContainer = Color(0xFF101E1A)

/** High elevation container fill for dialogs and bottom sheets in AMOLED mode. */
val AmoledSurfaceContainerHigh = Color(0xFF182B25)

/** Highest elevation container fill in AMOLED mode. */
val AmoledSurfaceContainerHighest = Color(0xFF213831)

/** Search input surface in AMOLED mode. */
val AmoledSurfaceVariant = Color(0xFF142420)

/** Soft cool slate gray secondary text and inactive icon color for AMOLED mode. */
val AmoledOnSurfaceVariant = Color(0xFF94A3B8)

/** Border outline color in AMOLED mode to maintain crisp card boundaries. */
val AmoledOutline = Color(0xFF28463E)

/** Subtle outline color for list dividers and card border strokes in AMOLED mode. */
val AmoledOutlineVariant = Color(0xFF1A302A)

// ============================================================================
// 4. Custom Semantic & Status Tokens
// ============================================================================

/** Success indicator green for Light mode. */
val LightSuccess = Color(0xFF059669)
val LightOnSuccess = Color(0xFFFFFFFF)
val LightSuccessContainer = Color(0xFFD1FAE5)

/** Success indicator green for Dark / AMOLED mode. */
val DarkSuccess = Color(0xFF34D399)
val DarkOnSuccess = Color(0xFF062016)
val DarkSuccessContainer = Color(0xFF064E3B)

/** Warning amber color for Light mode. */
val LightWarning = Color(0xFFD97706)
val LightOnWarning = Color(0xFFFFFFFF)
val LightWarningContainer = Color(0xFFFEF3C7)

/** Warning amber color for Dark / AMOLED mode. */
val DarkWarning = Color(0xFFFBBF24)
val DarkOnWarning = Color(0xFF451A03)
val DarkWarningContainer = Color(0xFF78350F)

/** Info status blue for Light mode. */
val LightInfo = Color(0xFF0284C7)
val LightOnInfo = Color(0xFFFFFFFF)
val LightInfoContainer = Color(0xFFE0F2FE)

/** Info status blue for Dark / AMOLED mode. */
val DarkInfo = Color(0xFF38BDF8)
val DarkOnInfo = Color(0xFF07364B)
val DarkInfoContainer = Color(0xFF075985)

/** Scrim background shadow color. */
val DefaultScrim = Color(0xFF000000)
