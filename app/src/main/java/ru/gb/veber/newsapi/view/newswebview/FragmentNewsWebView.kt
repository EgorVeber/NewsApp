package ru.gb.veber.newsapi.view.newswebview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentWebViewBinding
import ru.gb.veber.newsapi.presenter.FragmentNewsWebVIewPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.viewpagernews.DEFAULT_URL

class FragmentNewsWebView : MvpAppCompatFragment(), FragmentWebView, BackPressedListener {

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentNewsWebVIewPresenter by moxyPresenter {
        FragmentNewsWebVIewPresenter(App.instance.router,
            arguments?.getString(KEY_URL) ?: DEFAULT_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
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
        fun getInstance(url: String) = FragmentNewsWebView().apply {
            arguments = Bundle().apply {
                putString(KEY_URL, url)
            }
        }
    }

    override fun init(url: String) {
        binding.webNews.loadUrl(url)
        binding.webNews.settings.builtInZoomControls = true;
        binding.webNews.clearHistory()
        binding.webNews.clearCache(true)
    }
}