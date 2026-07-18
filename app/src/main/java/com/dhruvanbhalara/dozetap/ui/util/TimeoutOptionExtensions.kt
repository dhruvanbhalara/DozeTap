package com.dhruvanbhalara.dozetap.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.dhruvanbhalara.dozetap.R
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption

/**
 * Returns a localized short label for this [TimeoutOption] suitable for compact UI elements
 * (e.g., timeout preset chips, Quick Settings tile badge text).
 *
 * Resolves from the current locale's string resources. The domain model's [TimeoutOption.shortLabel]
 * remains untouched and is used only in non-Compose contexts (tests, canvas rendering, DataStore).
 */
@Composable
fun TimeoutOption.localizedShortLabel(): String = when {
    milliseconds == Int.MAX_VALUE -> stringResource(R.string.timeout_never_short)
    milliseconds < 60_000 -> stringResource(R.string.timeout_seconds_short, milliseconds / 1000)
    milliseconds < 3_600_000 -> stringResource(R.string.timeout_minutes_short, milliseconds / 60_000)
    else -> stringResource(R.string.timeout_hours_short, milliseconds / 3_600_000)
}

/**
 * Returns a localized display label for this [TimeoutOption] suitable for detailed settings rows
 * and dialogs, including proper plural forms (e.g., "1 Minute" vs "2 Minutes").
 *
 * Resolves from the current locale's plural string resources. The domain model's
 * [TimeoutOption.displayLabel] remains untouched and is used only in non-Compose contexts.
 */
@Composable
fun TimeoutOption.localizedDisplayLabel(): String = when {
    milliseconds == Int.MAX_VALUE -> stringResource(R.string.timeout_never_display)
    milliseconds < 60_000 -> {
        val seconds = milliseconds / 1000
        pluralStringResource(R.plurals.timeout_seconds_display, seconds, seconds)
    }
    milliseconds < 3_600_000 -> {
        val minutes = milliseconds / 60_000
        pluralStringResource(R.plurals.timeout_minutes_display, minutes, minutes)
    }
    else -> {
        val hours = milliseconds / 3_600_000
        pluralStringResource(R.plurals.timeout_hours_display, hours, hours)
    }
}
