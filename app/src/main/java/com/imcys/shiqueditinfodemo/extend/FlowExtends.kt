package com.imcys.shiqueditinfodemo.extend

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imcys.shiqueditinfodemo.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
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
