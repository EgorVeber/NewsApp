package ru.gb.veber.newsapi.presentation.webview

import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.gb.veber.newsapi.common.base.NewsViewModel
import javax.inject.Inject

class WebViewViewModel @Inject constructor(
    private val router: Router,
) : NewsViewModel() {

    private val loadingState: MutableStateFlow<WebViewState> =
        MutableStateFlow(WebViewState.StartedState)
    private val loadingFlow: StateFlow<WebViewState> = loadingState.asStateFlow()

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun subscribe(pageUrl: String): StateFlow<WebViewState> {
        loadingPage(pageUrl)
        return loadingFlow
    }

    fun successLoading() {
        loadingState.value = WebViewState.SuccessLoading
    }

    private fun loadingPage(pageUrl: String) {
        loadingState.value = WebViewState.StartLoading(pageUrl)
    }

    fun back() {
        router.exit()
    }

    sealed class WebViewState {
        data class StartLoading(val url: String) : WebViewState()
        object SuccessLoading : WebViewState()
        object StartedState : WebViewState()
    }
}