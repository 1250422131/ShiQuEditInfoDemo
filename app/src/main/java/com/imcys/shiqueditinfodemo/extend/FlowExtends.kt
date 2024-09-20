package com.imcys.shiqueditinfodemo.extend

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


inline fun <T> StateFlow<T>.collectState(
    owner: LifecycleOwner,
    crossinline onStateChanged: suspend CoroutineScope.(T) -> Unit
) {
    with(owner) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect { state ->
                    onStateChanged(state)
                }
            }
        }
    }
}
