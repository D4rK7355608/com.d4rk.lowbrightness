package com.d4rk.lowbrightness;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import com.d4rk.lowbrightness.databinding.ActivityMainBinding;
import com.d4rk.lowbrightness.helpers.AppUpdateNotificationsManager;
import com.d4rk.lowbrightness.helpers.AppUsageNotificationsManager;
import com.d4rk.lowbrightness.helpers.IShowHideScheduler;
import com.d4rk.lowbrightness.helpers.RequestDrawOverAppsPermission;
import com.d4rk.lowbrightness.notifications.SchedulerDisabledFragment;
import com.d4rk.lowbrightness.notifications.SchedulerEnabledFragment;
import com.d4rk.lowbrightness.services.OverlayService;
import com.d4rk.lowbrightness.ui.startup.StartupActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
public class MainActivity extends AppCompatActivity implements IShowHideScheduler {
    private AppBarConfiguration mAppBarConfiguration;
    private RequestDrawOverAppsPermission permissionRequester;
    private SharedPreferences sharedPreferences;
    private NavController navController;
    private AppUpdateManager appUpdateManager;
    private final int requestUpdateCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appSettings();
        setSupportActionBar(binding.appBarMain.toolbar);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        sharedPreferences = Prefs.get(this);
        DrawerLayout drawer = binding.layoutDrawer;
        NavigationView navigationView = binding.navigationView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_about, R.id.nav_settings).setOpenableLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.navigation_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        AppUpdateNotificationsManager appUpdateNotificationsManager = new AppUpdateNotificationsManager(this);
        appUpdateNotificationsManager.checkAndSendUpdateNotification();
    }
    public void appSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = sharedPreferences.getString(getString(R.string.key_language), getString(R.string.default_value_language));
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
    }
    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.close);
        builder.setMessage(R.string.summary_close);
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            MainActivity.super.onBackPressed();
            moveTaskToBack(true);
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        AppUsageNotificationsManager appUsageNotificationsManager = new AppUsageNotificationsManager(this);
        appUsageNotificationsManager.checkAndSendAppUsageNotification();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean preferenceFirebase = sharedPreferences.getBoolean(getString(R.string.key_firebase), true);
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(preferenceFirebase);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(preferenceFirebase);
        permissionRequester = new RequestDrawOverAppsPermission(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, requestUpdateCode);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
        startupScreen();
    }
    private void startupScreen() {
        SharedPreferences startupPreference = getSharedPreferences("startup", MODE_PRIVATE);
        if (startupPreference.getBoolean("value", true)) {
            startupPreference.edit().putBoolean("value", false).apply();
            startActivity(new Intent(this, StartupActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (permissionRequester.requestCodeMatches(requestCode)) {
            if (permissionRequester.canDrawOverlays()) {
                sharedPreferences.edit().putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, true).apply();
                startService(new Intent(this, OverlayService.class));
                Snackbar.make(findViewById(android.R.id.content), "Done! It was that easy.", Snackbar.LENGTH_LONG).show();
                invalidateOptionsMenu();
            } else {
                permissionRequester.requestPermissionDrawOverOtherApps();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        Application.refreshServices(this);
        super.onPause();
    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void showOrHideSchedulerUI(boolean show) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (show) {
            transaction.replace(R.id.linear_layout_scheduler, new SchedulerEnabledFragment());
        } else {
            transaction.replace(R.id.linear_layout_scheduler, new SchedulerDisabledFragment());
        }
        transaction.commit();
    }
}