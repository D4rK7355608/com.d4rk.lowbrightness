package com.d4rk.lowbrightness.app.brightness.domain.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> ViewModel.request(
    request: suspend () -> T,
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    finish: (() -> Unit)? = null,
    coroutineContext: CoroutineContext = Dispatchers.IO
): Job {
    return viewModelScope.launch(coroutineContext) {
        runCatching {
            request.invoke()
        }.onSuccess {
            success?.invoke(it)
        }.onFailure {
            it.printStackTrace()
            error?.invoke(it)
        }.also {
            finish?.invoke()
        }
    }
}
