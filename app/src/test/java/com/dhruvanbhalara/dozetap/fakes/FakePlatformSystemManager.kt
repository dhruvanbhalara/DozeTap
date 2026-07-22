package com.dhruvanbhalara.dozetap.fakes

import android.content.Intent
import com.dhruvanbhalara.dozetap.util.PlatformSystemManager

/**
 * Fake implementation of [PlatformSystemManager] for unit testing.
 */
class FakePlatformSystemManager : PlatformSystemManager {
    var isAddTileCalled = false
    var isAddWidgetCalled = false

    override fun requestAddTileNative(): Boolean {
        isAddTileCalled = true
        return true
    }

    override fun requestAddWidget(): Boolean {
        isAddWidgetCalled = true
        return true
    }

    override fun getWriteSettingsPermissionIntent(): Intent {
        return Intent()
    }
}
