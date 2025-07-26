package com.d4rk.lowbrightness.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.settings.utils.providers.AdvancedSettingsProvider
import com.d4rk.android.libs.apptoolkit.core.utils.constants.github.GithubConstants

class AppAdvancedSettingsProvider(val context : Context) : AdvancedSettingsProvider {
    override val bugReportUrl : String
        get() = "${GithubConstants.GITHUB_BASE}AppToolkit${GithubConstants.GITHUB_ISSUES_SUFFIX}"
}