package com.d4rk.lowbrightness.app.main.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ConsentManagerHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.editor
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissionsAndShow
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestSystemAlertWindowPermission
import com.d4rk.lowbrightness.app.brightness.domain.ext.sharedPreferences
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.main.domain.action.MainEvent
import com.d4rk.lowbrightness.core.data.datastore.DataStore
import com.d4rk.lowbrightness.ui.component.showToast
import com.d4rk.lowbrightness.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_AND_SHOW_ACTION = "requestPermissionsAndShow"
    }

    private lateinit var navController: NavController
    private var launchTimes: Int = -1

    private val dataStore : DataStore by inject()
    private lateinit var updateResultLauncher : ActivityResultLauncher<IntentSenderRequest>
    private lateinit var viewModel : MainViewModel
    private var keepSplashVisible : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup()
        if (launchTimes == -1) {
            launchTimes = sharedPreferences().getInt("launchTimes" , 0) + 1
            sharedPreferences().editor { putInt("launchTimes" , launchTimes % 20) }
        }

        /*setContent {
            val navController = rememberNavController()
            CompositionLocalProvider(LocalNavController provides navController) {
                this.navController = navController
                NightScreenTheme {
                    NavHost(
                        modifier = Modifier.Companion.fillMaxSize().background(MaterialTheme.colorScheme.background) ,
                        navController = navController ,
                        startDestination = HOME_SCREEN_ROUTE ,
                    ) {
                        composable(HOME_SCREEN_ROUTE) {
                            HomeScreen()
                        }
                        composable(
                            route = SETTINGS_SCREEN_ROUTE , deepLinks = listOf(navDeepLink { action = SETTINGS_SCREEN_ROUTE })
                        ) {
                            SettingsScreen()
                        }
                    }
                }
                doIntentAction(intent?.action)
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(event = MainEvent.CheckForUpdates)
        //checkUserConsent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        doIntentAction(intent.action)
    }

    private fun initializeDependencies() {
        CoroutineScope(context = Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
            ConsentManagerHelper.applyInitialConsent(dataStore = dataStore)
        }

        updateResultLauncher = registerForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {}

        viewModel = getViewModel { parametersOf(updateResultLauncher) }
    }

    private fun handleStartup() {
        lifecycleScope.launch {
            val isFirstLaunch : Boolean = dataStore.startup.first()
            keepSplashVisible = false
            if (isFirstLaunch) {
                startStartupActivity()
            }
            else {
                setMainActivityContent()
            }
        }
    }

    private fun startStartupActivity() {
        IntentsHelper.openActivity(context = this , activityClass = StartupActivity::class.java)
        finish()
    }

    private fun setMainActivityContent() {
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                    doIntentAction(intent?.action)
                }
            }
        }
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
                NightScreenReceiver.Companion.sendBroadcast(
                    context = this,
                    action = NightScreenReceiver.Companion.SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION
                )
            } else {
                startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })
    }
}