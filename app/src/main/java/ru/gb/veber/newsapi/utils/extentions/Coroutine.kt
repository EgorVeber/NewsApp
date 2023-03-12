package ru.gb.veber.newsapi.utils.extentions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class BaseCoroutineExceptionHandler(private val catchBlock: (t: Throwable) -> Unit) :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        catchBlock(exception)
    }
}

fun CoroutineScope.launchJob(
    catchBlock: (t: Throwable) -> Unit,
    finallyBlock: (() -> Unit)? = null,
    context: CoroutineContext = Dispatchers.Default,
    tryBlock: suspend CoroutineScope.() -> Unit,
): Job {
    return launch(context + BaseCoroutineExceptionHandler(catchBlock)) {
        try {
            tryBlock()
        } finally {
            finallyBlock?.invoke()
        }
    }
}

fun <T> Flow<T>.flowStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@flowStarted.collect()
    }
}

inline fun <reified T> Flow<T>.observeFlow(
    fragment: Fragment,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit,
): Job = fragment.viewLifecycleOwner.lifecycleScope.launchWhenStarted {
    flowWithLifecycle(fragment.viewLifecycleOwner.lifecycle, state).collect { item ->
        action(item)
    }
}

/**
 * Не хранит старые знаачения для новых подписчиков.
 * используется для сообщений, диалогов.
 */
@Suppress("FunctionName")
fun <T> SingleSharedFlow(): MutableSharedFlow<T> = MutableSharedFlow(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
