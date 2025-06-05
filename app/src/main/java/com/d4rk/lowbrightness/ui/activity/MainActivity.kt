package com.d4rk.lowbrightness.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.ext.editor
import com.d4rk.lowbrightness.ext.requestAllPermissionsAndShow
import com.d4rk.lowbrightness.ext.requestSystemAlertWindowPermission
import com.d4rk.lowbrightness.ext.sharedPreferences
import com.d4rk.lowbrightness.ui.NightScreenReceiver
import com.d4rk.lowbrightness.ui.component.showToast
import com.d4rk.lowbrightness.ui.local.LocalNavController
import com.d4rk.lowbrightness.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.screen.about.AboutScreen
import com.d4rk.lowbrightness.ui.screen.about.SponsorDialog
import com.d4rk.lowbrightness.ui.screen.home.HOME_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.screen.home.HomeScreen
import com.d4rk.lowbrightness.ui.screen.license.LICENSE_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.screen.license.LicenseScreen
import com.d4rk.lowbrightness.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.screen.settings.SettingsScreen
import com.d4rk.lowbrightness.ui.service.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.ui.theme.NightScreenTheme


class MainActivity : BaseComposeActivity() {
    companion object {
        const val REQUEST_PERMISSION_AND_SHOW_ACTION = "requestPermissionsAndShow"
    }

    private lateinit var navController: NavController
    private var launchTimes: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        if (launchTimes == -1) {
            launchTimes = sharedPreferences().getInt("launchTimes", 0) + 1
            sharedPreferences().editor { putInt("launchTimes", launchTimes % 20) }
        }

        setContent {
            val navController = rememberNavController()
            var openSponsorDialog by rememberSaveable { mutableStateOf(launchTimes == 20) }
            CompositionLocalProvider(LocalNavController provides navController) {
                this.navController = navController
                NightScreenTheme {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        navController = navController,
                        startDestination = HOME_SCREEN_ROUTE,
                    ) {
                        composable(HOME_SCREEN_ROUTE) {
                            HomeScreen()
                        }
                        composable(
                            route = SETTINGS_SCREEN_ROUTE,
                            deepLinks = listOf(navDeepLink { action = SETTINGS_SCREEN_ROUTE })
                        ) {
                            SettingsScreen()
                        }
                        composable(ABOUT_SCREEN_ROUTE) {
                            AboutScreen()
                        }
                        composable(LICENSE_SCREEN_ROUTE) {
                            LicenseScreen()
                        }
                    }

                    SponsorDialog(
                        visible = openSponsorDialog,
                        onClose = { openSponsorDialog = false }
                    )
                }
                doIntentAction(intent?.action)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        doIntentAction(intent?.action)
    }

    private fun doIntentAction(action: String?) {
        action ?: return
        if (action == REQUEST_PERMISSION_AND_SHOW_ACTION) {
            requestPermission()
        } else if (action == SETTINGS_SCREEN_ROUTE) {
            navController.navigate(SETTINGS_SCREEN_ROUTE) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isAccessibilityServiceRunning(this)) {
            requestAllPermissionsAndShow()
        } else {
            getString(R.string.no_accessibility_permission).showToast()
        }
    }

    private fun requestPermission() {
        requestSystemAlertWindowPermission(onGranted = {
            if (isAccessibilityServiceRunning(this)) {
                NightScreenReceiver.sendBroadcast(
                    context = this,
                    action = NightScreenReceiver.SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION
                )
            } else {
                startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })
    }
}
