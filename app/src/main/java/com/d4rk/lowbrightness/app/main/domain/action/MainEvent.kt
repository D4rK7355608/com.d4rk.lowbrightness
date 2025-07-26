package com.d4rk.lowbrightness.app.main.domain.action

import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.UiEvent

sealed interface MainEvent : UiEvent {
    data object LoadNavigation : MainEvent
}