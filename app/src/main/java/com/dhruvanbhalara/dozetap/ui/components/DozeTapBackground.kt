package com.dhruvanbhalara.dozetap.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Single-responsibility root window background container for DozeTap.
 * Guarantees that root screen layouts strictly consume [MaterialTheme.colorScheme.background]
 * (e.g. pure pitch black `#000000` in AMOLED mode and `#060D0B` in Dark mode),
 * decoupling root background styling from generic surface components.
 *
 * @param modifier Root layout modifier.
 * @param content Screen composable hierarchy.
 */
@Composable
fun DozeTapBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.fillMaxSize()
    ) {
        content()
    }
}
