package com.d4rk.lowbrightness.app.main.ui.components.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpActivity
import com.d4rk.android.libs.apptoolkit.app.main.ui.components.navigation.NavigationHost
import com.d4rk.android.libs.apptoolkit.app.settings.settings.ui.SettingsActivity
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.lowbrightness.app.brightness.ui.BrightnessScreen
import com.d4rk.lowbrightness.app.main.utils.constants.NavigationRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigationHost(
    navController : NavHostController , snackbarHostState : SnackbarHostState , onFabVisibilityChanged : (Boolean) -> Unit , paddingValues : PaddingValues
) {
    NavigationHost(
        navController = navController , startDestination = NavigationRoutes.ROUT_BRIGHTNESS
    ) {
        composable(route = NavigationRoutes.ROUT_BRIGHTNESS) {
            BrightnessScreen(paddingValues = paddingValues)
        }
    }
}

fun handleNavigationItemClick(
    context: Context,
    item: NavigationDrawerItem,
    drawerState: DrawerState? = null,
    coroutineScope: CoroutineScope? = null,
    onChangelogRequested: () -> Unit = {},
) {
    when (item.title) {
        R.string.settings -> IntentsHelper.openActivity(context = context , activityClass = SettingsActivity::class.java)
        R.string.help_and_feedback -> IntentsHelper.openActivity(context = context , activityClass = HelpActivity::class.java)
        R.string.updates -> onChangelogRequested()
        R.string.share -> IntentsHelper.shareApp(context = context , shareMessageFormat = R.string.summary_share_message)
    }
    if (drawerState != null && coroutineScope != null) {
        coroutineScope.launch { drawerState.close() }
    }
}