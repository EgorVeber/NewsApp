package ru.gb.veber.newsapi.view.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.WebViewFragmentBinding
import ru.gb.veber.newsapi.presenter.WebViewPresenter
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class WebViewFragment : MvpAppCompatFragment(), WebView, BackPressedListener {

    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: WebViewPresenter by moxyPresenter {
        WebViewPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
        private const val DEFAULT_URL = "https://ria.ru/"
        fun getInstance(url: String) = WebViewFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_URL, url)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webNews.webViewClient = webViewClient
        binding.webNews.loadUrl(arguments?.getString(KEY_URL) ?: DEFAULT_URL)
        binding.webNews.webChromeClient = WebChromeClient()
        binding.webNews.settings.builtInZoomControls = true
        binding.webNews.settings.displayZoomControls = false
        binding.webNews.clearHistory()
        binding.webNews.clearCache(true)

        binding.backWebView.setOnClickListener {
            presenter.back()
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
            super.onPageFinished(view, url)
            presenter.successLoading()
        }
    }

    override fun showPage() {
        binding.webNews.show()
        binding.progressBarWebView.hide()
    }
}