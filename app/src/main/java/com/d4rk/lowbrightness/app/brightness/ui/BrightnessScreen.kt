package com.d4rk.lowbrightness.app.brightness.ui

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.activity
import com.d4rk.lowbrightness.app.brightness.domain.ext.plus
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissions
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.components.ActionsCard
import com.d4rk.lowbrightness.app.brightness.ui.components.BottomImage
import com.d4rk.lowbrightness.app.brightness.ui.components.ColorCard
import com.d4rk.lowbrightness.app.brightness.ui.components.IntensityCard
import com.d4rk.lowbrightness.app.brightness.ui.components.ScheduleCard
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.ui.component.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrightnessScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current

    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isAccessibilityServiceRunning(context)) {
            context.activity.requestAllPermissions()
        } else {
            context.getString(R.string.no_accessibility_permission).showToast()
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp) + paddingValues
    ) {
        item { IntensityCard() }
        item { ColorCard() }
        item { ScheduleCard() }
        item {
            ActionsCard(
                onRunNightScreenClick = { requestAllPermissionsWithAccessibilityAndShow(context) },
                onRequestPermissionsClick = {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            )
        }
        item { BottomImage() }
    }
}