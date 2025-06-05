package com.d4rk.lowbrightness.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider
import com.d4rk.android.libs.apptoolkit.core.utils.constants.links.AppLinks

class AppAdvancedSettingsProvider(val context : Context) : AdvancedSettingsProvider {
    override val bugReportUrl : String
        get() = "${AppLinks.GITHUB_BASE}${context.packageName}${AppLinks.GITHUB_ISSUES_SUFFIX}"
}