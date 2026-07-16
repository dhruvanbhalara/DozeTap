package com.dhruvanbhalara.dozetap.service

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.collection.LruCache
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import com.dhruvanbhalara.dozetap.domain.usecase.CycleTimeoutUseCase
import com.dhruvanbhalara.dozetap.domain.usecase.GetScreenTimeoutUseCase
import com.dhruvanbhalara.dozetap.widget.refreshDozeTapWidgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * System Quick Settings TileService enabling one-tap screen timeout cycling from the Android notification shade.
 */
@AndroidEntryPoint
class DozeTapTileService : TileService() {

    @Inject
    lateinit var timeoutRepository: ITimeoutRepository

    @Inject
    lateinit var preferencesRepository: IPreferencesRepository

    @Inject
    lateinit var cycleTimeoutUseCase: CycleTimeoutUseCase

    @Inject
    lateinit var getScreenTimeoutUseCase: GetScreenTimeoutUseCase

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        serviceScope.launch {
            cycleTimeoutUseCase.cycleToNext()
            updateTileState()
            refreshDozeTapWidgets(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val current = getScreenTimeoutUseCase()

        tile.label = "DozeTap"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = current.displayLabel
        }

        tile.state = Tile.STATE_ACTIVE
        tile.icon = createTileTextIcon(applicationContext, current.shortLabel)
        tile.updateTile()
    }
}

/** LRU Cache for rendered Quick Settings tile bitmap icons to avoid garbage allocation on cycle. */
private val iconCache = LruCache<String, Icon>(16)

/**
 * Renders or retrieves cached text badge [Icon] for Quick Settings tile canvas.
 *
 * @param context Application context.
 * @param labelText Short duration text label to render inside tile icon.
 * @return Drawn or cached [Icon] containing text badge.
 */
fun createTileTextIcon(context: Context, labelText: String): Icon {
    val displayLabel = if (labelText.equals("Never", ignoreCase = true) || labelText.equals("Awake", ignoreCase = true)) {
        "∞"
    } else {
        labelText
    }

    iconCache.get(displayLabel)?.let { cachedIcon ->
        return cachedIcon
    }

    val density = context.resources.displayMetrics.density
    val size = (48 * density).toInt().coerceAtLeast(96)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
    }
    canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - (3f * density), bgPaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = if (displayLabel.length > 3) 13f * density else 17f * density
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    val fontMetrics = textPaint.fontMetrics
    val yOffset = (size / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
    canvas.drawText(displayLabel, size / 2f, yOffset, textPaint)

    val icon = Icon.createWithBitmap(bitmap)
    iconCache.put(displayLabel, icon)
    return icon
}

/**
 * Requests system Quick Settings tile refresh via [TileService.requestListeningState].
 *
 * @param context Context used to resolve ComponentName.
 */
fun requestDozeTapTileUpdate(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        TileService.requestListeningState(
            context,
            ComponentName(context, DozeTapTileService::class.java)
        )
    }
}
