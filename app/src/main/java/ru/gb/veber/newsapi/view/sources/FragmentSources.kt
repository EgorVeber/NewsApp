package ru.gb.veber.newsapi.view.sources

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.showText
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SourcesFragmentBinding
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.sources.recycler.SourcesListener
import javax.inject.Inject

class FragmentSources : Fragment(), BackPressedListener {

    private var _binding: SourcesFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sourcesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SourcesViewModel::class.java]
    }

    private val listener = object : SourcesListener {
        override fun openUrl(url: String?) {
            url?.let { urlString ->
                sourcesViewModel.openWebView(urlString)
            }
        }

        override fun imageClick(source: Sources) {
            sourcesViewModel.imageClick(source)
        }

        override fun newsClick(source: String?, name: String?) {
            sourcesViewModel.openAllNews(source, name)
        }
    }

    private val sourcesAdapter = FragmentSourcesAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SourcesFragmentBinding.inflate(inflater, container, false)
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
        return sourcesViewModel.onBackPressedRouter()
    }

    private fun initialization() {
        val accountId = arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT
        initViewModel(accountId)
        initView()
    }

    private fun initViewModel(accountId: Int) {
        sourcesViewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
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

    private fun initView() {
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
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}
