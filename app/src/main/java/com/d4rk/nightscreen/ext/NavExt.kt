package com.skyd.nightscreen.ext

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}

fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

fun NavController.popBackStackWithLifecycle() {
    if (currentBackStackEntry?.lifecycleIsResumed() == true) {
        popBackStack()
    }
}