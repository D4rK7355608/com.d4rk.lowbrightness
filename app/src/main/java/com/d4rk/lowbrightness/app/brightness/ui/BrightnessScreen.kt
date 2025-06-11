package com.d4rk.lowbrightness.app.brightness.ui

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.android.libs.apptoolkit.core.ui.components.ads.AdBanner
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
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
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.ShowAccessibilityDisclosure
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.ui.component.showToast
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrightnessScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val mediumRectangleAdConfig: AdsConfig =
        koinInject(qualifier = named(name = "banner_medium_rectangle"))
    val largeBannerAdConfig: AdsConfig = koinInject(qualifier = named(name = "large_banner"))
    var showAccessibilityDialog by remember { mutableStateOf(value = false) }
    var runAfterPermission by remember { mutableStateOf(value = false) }
    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isAccessibilityServiceRunning(context)) {
            if (runAfterPermission) {
                requestAllPermissionsWithAccessibilityAndShow(context)
            } else {
                context.activity.requestAllPermissions()
            }
        } else {
            context.getString(R.string.no_accessibility_permission).showToast()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                PaddingValues(
                    horizontal = 20.dp,
                ) + paddingValues
            ) ,
    ) {
        IntensityCard()
        ColorCard()
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = mediumRectangleAdConfig
        )
        ScheduleCard()
        ActionsCard(
            onRunNightScreenClick = {
                if (isAccessibilityServiceRunning(context)) {
                    requestAllPermissionsWithAccessibilityAndShow(context)
                } else {
                    runAfterPermission = true
                    showAccessibilityDialog = true
                }
            },
            onRequestPermissionsClick = {
                if (isAccessibilityServiceRunning(context)) {
                    context.activity.requestAllPermissions()
                } else {
                    runAfterPermission = false
                    showAccessibilityDialog = true
                }
            }
        )
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize),
            adsConfig = mediumRectangleAdConfig
        )
        BottomImage()
        AdBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SizeConstants.MediumSize), adsConfig = largeBannerAdConfig
        )
    }

    if (showAccessibilityDialog) {
        ShowAccessibilityDisclosure(
            onDismissRequest = { showAccessibilityDialog = false },
            onContinue = {
                showAccessibilityDialog = false
                startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        )
    }
}