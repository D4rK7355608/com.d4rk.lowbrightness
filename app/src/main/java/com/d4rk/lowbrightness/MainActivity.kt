package com.d4rk.lowbrightness

import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.d4rk.lowbrightness.base.Application
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.databinding.ActivityMainBinding
import com.d4rk.lowbrightness.helpers.AppUpdateNotificationsManager
import com.d4rk.lowbrightness.helpers.AppUsageNotificationsManager
import com.d4rk.lowbrightness.helpers.IShowHideScheduler
import com.d4rk.lowbrightness.helpers.RequestDrawOverAppsPermission
import com.d4rk.lowbrightness.notifications.SchedulerDisabledFragment
import com.d4rk.lowbrightness.notifications.SchedulerEnabledFragment
import com.d4rk.lowbrightness.services.OverlayService
import com.d4rk.lowbrightness.ui.startup.StartupActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity(), IShowHideScheduler {
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var permissionRequester: RequestDrawOverAppsPermission
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>
    private val requestUpdateCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overlayPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (permissionRequester.canDrawOverlays()) {
                    sharedPreferences.edit {
                        putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, true)
                    }
                    startService(Intent(this, OverlayService::class.java))
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Done! It was that easy.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    invalidateOptionsMenu()
                } else {
                    permissionRequester.requestPermissionDrawOverOtherApps(overlayPermissionLauncher)
                }
            }
        installSplashScreen()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appSettings()
        setSupportActionBar(binding.appBarMain.toolbar)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        sharedPreferences = Prefs.get(this)
        val drawer: DrawerLayout = binding.layoutDrawer
        val navigationView: NavigationView = binding.navigationView
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_about, R.id.nav_settings
        ).setOpenableLayout(drawer).build()
        navController = this.findNavController(R.id.navigation_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(navigationView, navController)
        val appUpdateNotificationsManager = AppUpdateNotificationsManager(this)
        appUpdateNotificationsManager.checkAndSendUpdateNotification()
    }

    fun appSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageCode = sharedPreferences.getString(
            getString(R.string.key_language),
            getString(R.string.default_value_language)
        )
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    override fun onBackPressed() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.close)
        builder.setMessage(R.string.summary_close)
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            super.onBackPressed()
            moveTaskToBack(true)
        }
        builder.setNegativeButton(android.R.string.no, null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        val appUsageNotificationsManager = AppUsageNotificationsManager(this)
        appUsageNotificationsManager.checkAndSendAppUsageNotification()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val preferenceFirebase = sharedPreferences.getBoolean(getString(R.string.key_firebase), true)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(preferenceFirebase)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = preferenceFirebase
        permissionRequester = RequestDrawOverAppsPermission(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    val updateOptions =
                        AppUpdateOptions
                            .newBuilder(AppUpdateType.FLEXIBLE)
                            .build()
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        updateOptions,
                        requestUpdateCode
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
        startupScreen()
    }

    private fun startupScreen() {
        val startupPreference = getSharedPreferences("startup", MODE_PRIVATE)
        if (startupPreference.getBoolean("value", true)) {
            startupPreference.edit { putBoolean("value", false) }
            startActivity(Intent(this, StartupActivity::class.java))
        }
    }


    override fun onPause() {
        Application.refreshServices(this)
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun showOrHideSchedulerUI(show: Boolean) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (show) {
            transaction.replace(R.id.linear_layout_scheduler, SchedulerEnabledFragment())
        } else {
            transaction.replace(R.id.linear_layout_scheduler, SchedulerDisabledFragment())
        }
        transaction.commit()
    }
}
