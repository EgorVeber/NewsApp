package ru.gb.veber.newsapi.view.newsitem

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentNewsViewPagerItemBinding
import ru.gb.veber.newsapi.model.ArticleDTO
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.presenter.FragmentNewsPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.viewpagernews.FragmentNewsAdapter

class FragmentNews : MvpAppCompatFragment(), FragmentNewsView, BackPressedListener {

    private var _binding: FragmentNewsViewPagerItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>
    private val newsAdapter = FragmentNewsAdapter()


    private val presenter: FragmentNewsPresenter by moxyPresenter {
        FragmentNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router, arguments?.getString(BUNDLE_KEY) ?: "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsViewPagerItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun init() {
        binding.recyclerNews.adapter = newsAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())
        bSheetB = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }
        binding.ArticleAll.setOnClickListener {
            bSheetB.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setSources(articles: List<ArticleDTO>) {
        TransitionManager.beginDelayedTransition(binding.root)
       // binding.ArticleAll.text = "Статей нашлось: " + articles.size
        newsAdapter.articles = articles
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {

                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    }

    companion object {
        private const val BUNDLE_KEY = "BUNDLE_KEY"
        fun getInstance(category: String) = FragmentNews().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_KEY, category)
            }
        }
    }
}