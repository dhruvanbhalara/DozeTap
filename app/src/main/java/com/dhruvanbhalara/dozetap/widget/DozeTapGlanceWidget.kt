package com.dhruvanbhalara.dozetap.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.dhruvanbhalara.dozetap.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.domain.usecase.CycleTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.GetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.SetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.ToggleKeepScreenOnUseCase

val CurrentMsKey = intPreferencesKey("current_ms")
val CurrentLabelKey = stringPreferencesKey("current_label")
val TargetMsKeyParam = ActionParameters.Key<Int>("target_ms")

/**
 * Hilt EntryPoint for injecting dependencies into Glance Widgets and ActionCallbacks.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface DozeTapWidgetEntryPoint {
    fun preferencesRepository(): IPreferencesRepository
    fun timeoutRepository(): ITimeoutRepository
    fun cycleTimeoutUseCase(): CycleTimeoutUseCase
    fun setScreenTimeoutUseCase(): SetScreenTimeoutUseCase
    fun toggleKeepScreenOnUseCase(): ToggleKeepScreenOnUseCase
    fun getScreenTimeoutUseCase(): GetScreenTimeoutUseCase
}

/**
 * Responsive Glance AppWidget delivering 1x1, 2x1, 4x1, and 4x4 home screen widgets.
 */
class DozeTapGlanceWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 80.dp)
        private val MEDIUM_RECT = DpSize(180.dp, 80.dp)
        private val LARGE_RECT = DpSize(260.dp, 80.dp)
        private val EXTRA_LARGE_SQUARE = DpSize(240.dp, 200.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE, MEDIUM_RECT, LARGE_RECT, EXTRA_LARGE_SQUARE)
    )

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DozeTapWidgetEntryPoint::class.java
        )
        val currentTimeout = entryPoint.getScreenTimeoutUseCase().invoke()

        provideContent {
            val currentLabel = currentState(CurrentLabelKey) ?: currentTimeout.shortLabel
            val size = LocalSize.current

            GlanceTheme {
                when {
                    size.width >= EXTRA_LARGE_SQUARE.width && size.height >= EXTRA_LARGE_SQUARE.height -> {
                        ExtraLargeWidgetLayout(currentLabel = currentLabel)
                    }
                    size.width >= LARGE_RECT.width -> {
                        LargeWidgetLayout(currentLabel = currentLabel)
                    }
                    size.width >= MEDIUM_RECT.width -> {
                        MediumWidgetLayout(currentLabel = currentLabel)
                    }
                    else -> {
                        SmallWidgetLayout(currentLabel = currentLabel)
                    }
                }
            }
        }
    }

    @Composable
    private fun ExtraLargeWidgetLayout(currentLabel: String) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(28.dp)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ctx = LocalContext.current
                Text(
                    text = ctx.getString(R.string.glance_app_name),
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSurface
                    )
                )
            }

            val allOptions = TimeoutOption.DEFAULT_PRESETS
            val rows = allOptions.chunked(4)

            rows.forEach { rowItems ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .padding(vertical = 4.dp)
                ) {
                    rowItems.forEach { option ->
                        val isActive = currentLabel == option.shortLabel
                        val containerBg = if (isActive) {
                            GlanceTheme.colors.primary
                        } else {
                            GlanceTheme.colors.surfaceVariant
                        }
                        val contentColor = if (isActive) {
                            GlanceTheme.colors.onPrimary
                        } else {
                            GlanceTheme.colors.onSurfaceVariant
                        }

                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .fillMaxHeight()
                                .padding(horizontal = 4.dp)
                                .background(containerBg)
                                .cornerRadius(16.dp)
                                .clickable(
                                    actionRunCallback<SetPresetActionCallback>(
                                        actionParametersOf(TargetMsKeyParam to option.milliseconds)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.shortLabel,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = contentColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SmallWidgetLayout(currentLabel: String) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(24.dp)
                .clickable(actionRunCallback<CycleTimeoutActionCallback>())
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱",
                    style = TextStyle(fontSize = 18.sp)
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = currentLabel,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )
            }
        }
    }

    @Composable
    private fun MediumWidgetLayout(currentLabel: String) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.secondaryContainer)
                .cornerRadius(24.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ctx = LocalContext.current
                Text(
                    text = ctx.getString(R.string.glance_timeout_label),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
                Text(
                    text = currentLabel,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onSecondaryContainer
                    )
                )
            }

            Box(
                modifier = GlanceModifier
                    .width(44.dp)
                    .height(44.dp)
                    .background(GlanceTheme.colors.onSecondaryContainer)
                    .cornerRadius(22.dp)
                    .clickable(actionRunCallback<CycleTimeoutActionCallback>()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔄",
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }

    @Composable
    private fun LargeWidgetLayout(currentLabel: String) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(24.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val presets = listOf(
                TimeoutOption.FIFTEEN_SEC,
                TimeoutOption.ONE_MIN,
                TimeoutOption.FIVE_MIN,
                TimeoutOption.TEN_MIN
            )

            presets.forEach { option ->
                val isActive = currentLabel == option.shortLabel
                val bg = if (isActive) GlanceTheme.colors.primary else GlanceTheme.colors.surfaceVariant
                val textColor = if (isActive) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant

                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp)
                        .background(bg)
                        .cornerRadius(16.dp)
                        .clickable(
                            actionRunCallback<SetPresetActionCallback>(
                                actionParametersOf(TargetMsKeyParam to option.milliseconds)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.shortLabel,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.width(4.dp))

            val isKeepAwakeActive = currentLabel == "Never" || currentLabel.lowercase().contains("awake")
            val toggleBg = if (isKeepAwakeActive) GlanceTheme.colors.primaryContainer else GlanceTheme.colors.secondaryContainer

            Box(
                modifier = GlanceModifier
                    .width(44.dp)
                    .fillMaxHeight()
                    .background(toggleBg)
                    .cornerRadius(16.dp)
                    .clickable(actionRunCallback<ToggleKeepAwakeActionCallback>()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☕",
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }
}

/** Glance ActionCallback handling cycle-to-next timeout action from widget tap. */
class CycleTimeoutActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DozeTapWidgetEntryPoint::class.java
        )
        entryPoint.cycleTimeoutUseCase().cycleToNext()
        refreshDozeTapWidgets(context)
    }
}

/** Glance ActionCallback setting specific [TimeoutOption] from widget button tap. */
class SetPresetActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val targetMs = parameters[TargetMsKeyParam] ?: return
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DozeTapWidgetEntryPoint::class.java
        )
        val option = TimeoutOption.fromMilliseconds(targetMs)
        entryPoint.setScreenTimeoutUseCase().invoke(option)
        refreshDozeTapWidgets(context)
    }
}

/** Glance ActionCallback toggling Keep Screen On state from widget tap. */
class ToggleKeepAwakeActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DozeTapWidgetEntryPoint::class.java
        )
        entryPoint.toggleKeepScreenOnUseCase().invoke()
        refreshDozeTapWidgets(context)
    }
}

/** BroadcastReceiver hosting Glance widget. */
class DozeTapWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DozeTapGlanceWidget()
}

/**
 * Triggers state update across all active Glance AppWidget instances and updates Quick Settings Tile.
 *
 * @param context Application context.
 */
suspend fun refreshDozeTapWidgets(context: Context) {
    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        DozeTapWidgetEntryPoint::class.java
    )
    val current = entryPoint.getScreenTimeoutUseCase().invoke()

    // Build a localized label for the current timeout using context strings
    val localizedLabel: String = when {
        current.milliseconds == Int.MAX_VALUE -> context.getString(R.string.timeout_never_short)
        current.milliseconds < 60_000 -> context.getString(
            R.string.timeout_seconds_short, current.milliseconds / 1000
        )
        current.milliseconds < 3_600_000 -> context.getString(
            R.string.timeout_minutes_short, current.milliseconds / 60_000
        )
        else -> context.getString(
            R.string.timeout_hours_short, current.milliseconds / 3_600_000
        )
    }

    val widget = DozeTapGlanceWidget()
    val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(DozeTapGlanceWidget::class.java)

    glanceIds.forEach { id ->
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
            prefs.toMutablePreferences().apply {
                this[CurrentMsKey] = current.milliseconds
                this[CurrentLabelKey] = localizedLabel
            }
        }
        widget.update(context, id)
    }

    com.dhruvanbhalara.dozetap.service.requestDozeTapTileUpdate(context)
}

