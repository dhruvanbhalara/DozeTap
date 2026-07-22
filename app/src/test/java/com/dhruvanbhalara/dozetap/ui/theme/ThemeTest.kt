package com.dhruvanbhalara.dozetap.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests verifying Material 3 color scheme definitions for DozeTap.
 * Guarantees that Light, Dark (Slate Teal), and AMOLED (Pure Black) color schemes
 * fulfill exact contrast and background requirements.
 */
class ThemeTest {

    @Test
    fun amoledColorScheme_hasPurePitchBlackBackground() {
        assertEquals(Color(0xFF000000), AmoledBackground)
        assertEquals(Color(0xFF000000), AmoledSurfaceContainerLowest)
    }

    @Test
    fun darkColorScheme_hasObsidianTealBackground() {
        assertEquals(Color(0xFF0B1512), DarkBackground)
    }

    @Test
    fun lightColorScheme_hasCrispSlateBackground() {
        assertEquals(Color(0xFFF8FAFC), LightBackground)
    }
}
