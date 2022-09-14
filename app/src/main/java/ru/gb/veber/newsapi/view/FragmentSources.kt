package ru.gb.veber.newsapi.view

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
import ru.gb.veber.newsapi.databinding.FragmentSourcesBinding
import ru.gb.veber.newsapi.model.data.SourcesDTO
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.NewsRetrofit
import ru.gb.veber.newsapi.presenter.FragmentSourcesPresenter
import ru.gb.veber.newsapi.presenter.FragmentSourcesView

class FragmentSources : MvpAppCompatFragment(), FragmentSourcesView,BackPressedListener {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!
    private val sourcesAdapter = FragmentSourcesAdapter()

    private val presenter: FragmentSourcesPresenter by moxyPresenter {
        FragmentSourcesPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle), App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSourcesBinding.inflate(inflater, container, false)
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
      return  presenter.onBackPressedRouter()
    }
}