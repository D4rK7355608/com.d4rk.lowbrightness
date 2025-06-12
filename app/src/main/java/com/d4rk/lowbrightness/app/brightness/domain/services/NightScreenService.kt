package com.d4rk.lowbrightness.app.brightness.domain.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.d4rk.lowbrightness.app.brightness.ui.components.closeNightScreen
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.app.brightness.ui.components.showNightScreenLayer

@RequiresApi(Build.VERSION_CODES.N)
class NightScreenService : TileService() {
    override fun onStartListening() {
        super.onStartListening()

        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter().apply {
                addAction(ACTION_ACTIVE_TILE)
                addAction(ACTION_INACTIVE_TILE)
            },
            ContextCompat.RECEIVER_EXPORTED,
        )

        qsTile.state = if (showNightScreenLayer) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        unregisterReceiver(receiver)
    }

    override fun onClick() {
        super.onClick()

        val tile = qsTile ?: return

        if (tile.state == Tile.STATE_INACTIVE) {
            requestAllPermissionsWithAccessibilityAndShow(this)
        } else {
            closeNightScreen()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val tile = qsTile ?: return
            when (intent?.action) {
                ACTION_ACTIVE_TILE -> {
                    tile.state = Tile.STATE_ACTIVE
                }

                ACTION_INACTIVE_TILE -> {
                    tile.state = Tile.STATE_INACTIVE
                }
            }
            tile.updateTile()
        }
    }

    companion object {
        const val ACTION_ACTIVE_TILE = "active"
        const val ACTION_INACTIVE_TILE = "inactive"
    }
}