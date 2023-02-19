package ru.gb.veber.newsapi.view.webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.model.Account
import javax.inject.Inject

class WebViewViewModel @Inject constructor(
    private val router: Router,
) : ViewModel() {

    private val mutableFlow: MutableLiveData<WebViewState> = MutableLiveData()
    private val flow: LiveData<WebViewState> = mutableFlow

    fun subscribe(): LiveData<WebViewState> {
        return flow
    }

    fun successLoading() {
        mutableFlow.value = WebViewState.ShowPage
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun back() {
        router.exit()
    }

    sealed class WebViewState {
        object ShowPage : WebViewState()
    }
}