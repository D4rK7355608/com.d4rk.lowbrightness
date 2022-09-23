package com.d4rk.lowbrightness;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import com.d4rk.lowbrightness.databinding.ActivityMainBinding;
import com.d4rk.lowbrightness.helpers.IShowHideScheduler;
import com.d4rk.lowbrightness.helpers.RequestDrawOverAppsPermission;
import com.d4rk.lowbrightness.notifications.SchedulerDisabledFragment;
import com.d4rk.lowbrightness.notifications.SchedulerEnabledFragment;
import com.d4rk.lowbrightness.services.OverlayService;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
public class MainActivity extends AppCompatActivity implements IShowHideScheduler{
    private AppBarConfiguration mAppBarConfiguration;
    private RequestDrawOverAppsPermission permissionRequester;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        FirebaseApp.initializeApp(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        sharedPreferences = Prefs.get(getBaseContext());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_about, R.id.nav_settings).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    @Override
    protected void onResume() {
        super.onResume();
        permissionRequester = new RequestDrawOverAppsPermission(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (permissionRequester.requestCodeMatches(requestCode)) {
            if (permissionRequester.canDrawOverlays()) {
                sharedPreferences.edit().putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, true).apply();
                getBaseContext().startService(new Intent(getBaseContext(), OverlayService.class));
                Snackbar.make(findViewById(android.R.id.content), "Done! It was that easy.", Snackbar.LENGTH_LONG).show();
                recreate();
            } else {
                permissionRequester.requestPermissionDrawOverOtherApps();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        Application.refreshServices(getBaseContext());
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    @Override
    public void showOrHideSchedulerUI(boolean show) {
        if (show) {
            getSupportFragmentManager().beginTransaction().replace(R.id.llSchedulerContainer, new SchedulerEnabledFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.llSchedulerContainer, new SchedulerDisabledFragment()).commit();
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}