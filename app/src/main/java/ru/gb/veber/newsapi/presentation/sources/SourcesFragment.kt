package ru.gb.veber.newsapi.presentation.sources

import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.common.getAppComponent
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.sources.recycler.FragmentSourcesAdapter
import ru.gb.veber.newsapi.presentation.sources.recycler.SourcesListener
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.coroutine.observeFlow
import ru.gb.veber.ui_common.utils.BundleInt
import ru.gb.veber.ui_core.databinding.SourcesFragmentBinding
import ru.gb.veber.ui_core.extentions.showSnackBar

class SourcesFragment : NewsFragment<SourcesFragmentBinding, SourcesViewModel>(
    SourcesFragmentBinding::inflate
) {

    private var accountId: Int by BundleInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)
    private var lastFocusSelected = FOCUS_DEFAULT
    override fun getViewModelClass(): Class<SourcesViewModel> = SourcesViewModel::class.java

    override fun onInject() = getAppComponent().inject(this)

    private val listener = object : SourcesListener {
        override fun openUrl(url: String?) {
            url?.let { urlString ->
                viewModel.openWebView(urlString)
            }
        }

        override fun imageClick(source: SourcesModel) {
            viewModel.imageClick(source)
        }

        override fun newsClick(source: String?, name: String?) {
            viewModel.openAllNews(source, name)
        }

        override fun focus(source: SourcesModel, type: Int) {
            viewModel.focusOne(source, type)
        }
    }

    private val sourcesAdapter = FragmentSourcesAdapter(listener)

    override fun onObserveData() {
        viewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
            when (state) {
                is SourcesViewModel.SourcesState.SetSources -> {
                    setSources(state.list)
                }
            }
        }
        viewModel.showMessageFlow.observeFlow(this) {
            setLogin()
        }
    }

    override fun onInitView() {
        binding.recyclerSources.adapter = sourcesAdapter
        binding.recyclerSources.itemAnimator = null
        binding.recyclerSources.layoutManager = LinearLayoutManager(requireContext())
        binding.spinnerShowBy.setSelection(FOCUS_DEFAULT, false)
        binding.spinnerShowBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (lastFocusSelected != position) {
                    lastFocusSelected = position
                    viewModel.focusAll(position)
                }
            }

        }
        binding.filter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) viewModel.setFilter(binding.filter.text.toString())
            false
        }
    }

    private fun setSources(list: List<SourcesModel>) {
        TransitionManager.beginDelayedTransition(binding.root)
        sourcesAdapter.sources = list
    }

    private fun setLogin() {
        this.showSnackBar(getString(UiCoreStrings.loginAddToFavorites))
    }

    companion object {
        const val FOCUS_NAME = 0
        const val FOCUS_BRIF = 1
        const val FOCUS_DEFAULT = 1
        fun getInstance(accountID: Int): SourcesFragment {
            return SourcesFragment().apply {
                this.accountId = accountID
            }
        }
    }
}
