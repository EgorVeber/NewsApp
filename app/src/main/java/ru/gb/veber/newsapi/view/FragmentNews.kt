package ru.gb.veber.newsapi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.databinding.FragmentNewsBinding
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.NewsRetrofit
import ru.gb.veber.newsapi.presenter.FragmentNewsPresenter
import ru.gb.veber.newsapi.presenter.FragmentNewsView
import ru.gb.veber.newsapi.presenter.FragmentSourcesPresenter

class FragmentNews : MvpAppCompatFragment(), FragmentNewsView {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val presenter: FragmentNewsPresenter by moxyPresenter {
        FragmentNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}