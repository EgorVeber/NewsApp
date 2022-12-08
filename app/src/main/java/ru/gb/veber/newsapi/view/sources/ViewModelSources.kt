package ru.gb.veber.newsapi.view.sources

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.gb.veber.newsapi.utils.subscribeDefault

typealias StEmpty = ViewModelSources.SVState.Empty

class RepoTest {
    fun getSources2(): Single<List<Int>> {
        return Single.create {
            it.onSuccess(listOf(1, 2, 3, 4, 5, 6, 77))
        }.subscribeDefault()
    }
}

class ViewModelSources : ViewModel() {

    private val viewState: MutableStateFlow<SVState> = MutableStateFlow(StEmpty)
    fun getViewStat(): StateFlow<SVState> = viewState

    fun getSources() {
        viewState.value = SVState.Loading
        RepoTest().getSources2().subscribe({
            viewState.value = SVState.Success(it)
        }, {
            viewState.value = SVState.Error("Error")
        })
    }

    sealed interface SVState {
        object Loading : SVState
        object Empty : SVState
        class Error(val message: String) : SVState
        class Success(val list: List<Int>) : SVState
    }
}
fun <T> Single<T>.toDeferred(coroutineScope: CoroutineScope) = coroutineScope.async { await() }
