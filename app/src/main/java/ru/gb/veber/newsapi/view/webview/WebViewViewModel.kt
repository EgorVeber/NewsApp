package ru.gb.veber.newsapi.view.webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class WebViewViewModel @Inject constructor(
    private val router: Router,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<WebViewState> = MutableLiveData()
    private val flow: LiveData<WebViewState> = mutableFlow

    fun subscribe(pageUrl: String): LiveData<WebViewState> {
        loadingPage(pageUrl)
        return flow
    }

    fun successLoading() {
        mutableFlow.value = WebViewState.SuccessLoading
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun back() {
        router.exit()
    }

    private fun loadingPage(pageUrl: String) {
        mutableFlow.value = WebViewState.LoadingPage(pageUrl)
    }

    sealed class WebViewState {
        object SuccessLoading : WebViewState()
        data class LoadingPage(val url:String) : WebViewState()
    }
}