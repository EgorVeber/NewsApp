package ru.gb.veber.newsapi.presentation.sources

import android.transition.TransitionManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.showText
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SourcesFragmentBinding
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.presentation.sources.recycler.SourcesListener
import ru.gb.veber.newsapi.view.sources.FragmentSourcesAdapter

class FragmentSources : NewsFragment<SourcesFragmentBinding, SourcesViewModel>(
    SourcesFragmentBinding::inflate) {

    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    override fun getViewModelClass(): Class<SourcesViewModel> = SourcesViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    private val listener = object : SourcesListener {
        override fun openUrl(url: String?) {
            url?.let { urlString ->
                viewModel.openWebView(urlString)
            }
        }

        override fun imageClick(source: Sources) {
            viewModel.imageClick(source)
        }

        override fun newsClick(source: String?, name: String?) {
            viewModel.openAllNews(source, name)
        }
    }

    private val sourcesAdapter = FragmentSourcesAdapter(listener)

    override fun onObserveData() {
       viewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
            when (state) {
                is SourcesViewModel.SourcesState.SetSources -> {
                    setSources(state.list)
                }
                SourcesViewModel.SourcesState.ShowToastLogIn -> {
                    setLogin()
                }
            }
        }
    }

    override fun onInitView() {
        binding.recyclerSources.adapter = sourcesAdapter
        binding.recyclerSources.itemAnimator = null
        binding.recyclerSources.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setSources(list: List<Sources>) {
        TransitionManager.beginDelayedTransition(binding.root)
        sourcesAdapter.sources = list
    }

    private fun setLogin() {
        binding.root.showText(getString(R.string.loginAddToFavorites))
    }

    companion object {
        fun getInstance(accountID: Int): FragmentSources {
            return FragmentSources().apply {
                this.accountId = accountID
            }
        }
    }
}