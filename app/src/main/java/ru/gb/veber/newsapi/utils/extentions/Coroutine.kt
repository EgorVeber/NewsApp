package ru.gb.veber.newsapi.utils.extentions

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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

fun <T> Flow<T>.flowStarted(lifecycleScope: LifecycleCoroutineScope,) {
    lifecycleScope.launchWhenStarted {
        this@flowStarted.collect()
    }
}
