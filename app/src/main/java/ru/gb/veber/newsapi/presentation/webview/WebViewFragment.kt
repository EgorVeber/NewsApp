package ru.gb.veber.newsapi.presentation.webview

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.ui_common.coroutine.observeFlow
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_common.utils.BundleString
import ru.gb.veber.ui_core.databinding.WebViewFragmentBinding

class WebViewFragment :
    NewsFragment<WebViewFragmentBinding, WebViewViewModel>(WebViewFragmentBinding::inflate) {

    private var pageUrl: String by BundleString(KEY_URL, DEFAULT_URL)

    private val mWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
            super.onPageFinished(view, url)
            viewModel.successLoading()
        }
    }

    override fun getViewModelClass(): Class<WebViewViewModel> = WebViewViewModel::class.java


    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onInitView() {
        with(binding.webNews) {
            webViewClient = mWebViewClient
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            clearHistory()
            clearCache(true)
        }

        binding.backWebView.setOnClickListener {
            viewModel.back()
        }
    }

    override fun onObserveData() {
        viewModel.subscribe(pageUrl).observeFlow(this) { state ->
            when (state) {
                is WebViewViewModel.WebViewState.StartLoading -> {
                    binding.webNews.loadUrl(state.url)
                }
                WebViewViewModel.WebViewState.SuccessLoading -> {
                    successLoading()
                }
                WebViewViewModel.WebViewState.StartedState -> {}
            }
        }
    }

    private fun successLoading() {
        binding.webNews.show()
        binding.progressBarWebView.hide()
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
        private const val DEFAULT_URL = "https://ria.ru/"
        fun getInstance(url: String) = WebViewFragment().apply {
            this.pageUrl = url
        }
    }
}
