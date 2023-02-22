package ru.gb.veber.newsapi.view.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.WebViewFragmentBinding
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import javax.inject.Inject

class WebViewFragment : Fragment(), BackPressedListener {

    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val webViewViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WebViewViewModel::class.java]
    }

    private val mWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
            super.onPageFinished(view, url)
            webViewViewModel.successLoading()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        initialization()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return webViewViewModel.onBackPressedRouter()
    }

    private fun initialization() {
        val pageUrl = arguments?.getString(KEY_URL) ?: DEFAULT_URL
        initViewModel(pageUrl)
        initView()
    }

    private fun initViewModel(pageUrl: String) {
        webViewViewModel.subscribe(pageUrl).observe(viewLifecycleOwner) { state ->
            when (state) {
                WebViewViewModel.WebViewState.SuccessLoading -> {
                    successLoading()
                }
                is WebViewViewModel.WebViewState.LoadingPage -> {
                    binding.webNews.loadUrl(state.url)
                }
            }
        }
    }

    private fun initView() {
        with(binding.webNews) {
            webViewClient = mWebViewClient
            webChromeClient = WebChromeClient()
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            clearHistory()
            clearCache(true)
        }

        binding.backWebView.setOnClickListener {
            webViewViewModel.back()
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
            arguments = Bundle().apply {
                putString(KEY_URL, url)
            }
        }
    }
}