package com.d4rk.lowbrightness.core.di.modules


import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.android.libs.apptoolkit.data.client.KtorClient
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.lowbrightness.app.main.ui.MainViewModel
import com.d4rk.lowbrightness.app.onboarding.utils.interfaces.providers.AppOnboardingProvider
import com.d4rk.lowbrightness.core.data.datastore.DataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule : Module = module {
    single<DataStore> { DataStore.getInstance(context = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get() , get()) }
    single { KtorClient().createClient() }

    single<OnboardingProvider> { AppOnboardingProvider() }

    viewModel { (launcher : ActivityResultLauncher<IntentSenderRequest>) ->
        MainViewModel(dispatcherProvider = get())
    }
}