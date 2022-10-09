package ru.gb.veber.newsapi.view.sources

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SourcesFragmentBinding
import ru.gb.veber.newsapi.model.SourcesDTO
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.presenter.SourcesPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerFragment

class FragmentSources : MvpAppCompatFragment(), FragmentSourcesView, BackPressedListener {

    private var _binding: SourcesFragmentBinding? = null
    private val binding get() = _binding!!
    private val sourcesAdapter = FragmentSourcesAdapter()

    private val presenter: SourcesPresenter by moxyPresenter {
        SourcesPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle), App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SourcesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {
        binding.recyclerSources.adapter = sourcesAdapter
        binding.recyclerSources.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("SetTextI18n")
    override fun setSources(list: List<SourcesDTO>) {
        TransitionManager.beginDelayedTransition(binding.root)
        sourcesAdapter.sources = list
        binding.listCountSources.text = "Всего издательств (источников): ${list.size} "
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
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