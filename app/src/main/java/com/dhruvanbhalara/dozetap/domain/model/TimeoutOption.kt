package com.dhruvanbhalara.dozetap.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents a screen off timeout duration option.
 *
 * Encapsulates the millisecond value alongside human-readable short and display labels
 * suitable for UI chips, Quick Settings tiles, and Glance widgets.
 *
 * @property milliseconds The timeout duration in milliseconds (or [Int.MAX_VALUE] for Keep Awake / Never).
 * @property shortLabel Abbreviated label for compact UI elements (e.g., "30s", "5m").
 * @property displayLabel Full descriptive label for detailed settings (e.g., "30 Seconds", "5 Minutes").
 */
@Immutable
data class TimeoutOption(
    val milliseconds: Int,
    val shortLabel: String,
    val displayLabel: String
) {
    companion object {
        val FIFTEEN_SEC = TimeoutOption(15_000, "15s", "15 Seconds")
        val THIRTY_SEC = TimeoutOption(30_000, "30s", "30 Seconds")
        val ONE_MIN = TimeoutOption(60_000, "1m", "1 Minute")
        val TWO_MIN = TimeoutOption(120_000, "2m", "2 Minutes")
        val FIVE_MIN = TimeoutOption(300_000, "5m", "5 Minutes")
        val TEN_MIN = TimeoutOption(600_000, "10m", "10 Minutes")
        val THIRTY_MIN = TimeoutOption(1_800_000, "30m", "30 Minutes")
        val NEVER = TimeoutOption(Int.MAX_VALUE, "Never", "Never (24 Hours)")

        /**
         * Default ordered presets available in the app dashboard, Quick Settings tile cycle, and widgets.
         */
        val DEFAULT_PRESETS: List<TimeoutOption> = listOf(
            FIFTEEN_SEC,
            THIRTY_SEC,
            ONE_MIN,
            TWO_MIN,
            FIVE_MIN,
            TEN_MIN,
            THIRTY_MIN,
            NEVER
        )

        /**
         * Matches or creates a [TimeoutOption] corresponding to the given duration in milliseconds.
         *
         * @param ms The duration in milliseconds.
         * @return The exact preset matching [ms], nearest preset match, or a custom formatted [TimeoutOption].
         */
        fun fromMilliseconds(ms: Int): TimeoutOption {
            if (ms >= 86_400_000 || ms == Int.MAX_VALUE) return NEVER
            return DEFAULT_PRESETS.minByOrNull { kotlin.math.abs(it.milliseconds - ms) }
                ?: TimeoutOption(ms, formatShortLabel(ms), formatDisplayLabel(ms))
        }

        private fun formatShortLabel(ms: Int): String {
            val seconds = ms / 1000
            return when {
                seconds < 60 -> "${seconds}s"
                seconds < 3600 -> "${seconds / 60}m"
                else -> "${seconds / 3600}h"
            }
        }

        private fun formatDisplayLabel(ms: Int): String {
            val seconds = ms / 1000
            return when {
                seconds < 60 -> "$seconds Seconds"
                seconds < 3600 -> "${seconds / 60} Minute${if (seconds / 60 > 1) "s" else ""}"
                else -> "${seconds / 3600} Hour${if (seconds / 3600 > 1) "s" else ""}"
            }
        }
    }
}
