package com.d4rk.lowbrightness.core.di.modules


import com.d4rk.android.libs.apptoolkit.app.startup.utils.interfaces.providers.StartupProvider
import com.d4rk.android.libs.apptoolkit.app.support.domain.usecases.QueryProductDetailsUseCase
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportViewModel
import com.d4rk.lowbrightness.app.startup.utils.interfaces.providers.AppStartupProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appToolkitModule : Module = module {
    single<StartupProvider> { AppStartupProvider() }

    single<QueryProductDetailsUseCase> { QueryProductDetailsUseCase() }
    viewModel {
        SupportViewModel(queryProductDetailsUseCase = get() , dispatcherProvider = get())
    }
}