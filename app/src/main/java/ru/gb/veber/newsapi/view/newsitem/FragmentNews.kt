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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import io.reactivex.rxjava3.core.Observable
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FragmentNewsViewPagerItemBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.presenter.FragmentNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.viewpagernews.CATEGORY_GENERAL
import ru.gb.veber.newsapi.view.viewpagernews.FragmentNewsAdapter


class FragmentNews : MvpAppCompatFragment(), FragmentNewsView, BackPressedListener {

    private var _binding: FragmentNewsViewPagerItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>
    private val newsAdapter = FragmentNewsAdapter() {
        presenter.clickNews(it)
    }

    private val presenter: FragmentNewsPresenter by moxyPresenter {
        FragmentNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsViewPagerItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadNews(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL)
        binding.imageViewAll.setOnClickListener {
            bSheetB.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private var listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                Log.d("recyclerNews", "Нижняя точка 1 ")
            }
        }
    }

    override fun init() {
        binding.recyclerNews.adapter = newsAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())
        bSheetB = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }

        Observable.create<Boolean> {
            it.onNext(true)
        }.subscribe()
        //TODO Придумать пагинацию
        binding.recyclerNews.addOnScrollListener(listener)
        // bSheetB.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        var flag = false
        binding.filterButton.setOnClickListener {
            Log.d("flag", flag.toString())
            if (!flag) {
                flag = true
                bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
                binding.imageViewAll.show()
                binding.imageViewDown.show()
            } else {
                flag = false
                bSheetB.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.imageViewAll.hide()
                binding.imageViewDown.hide()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setSources(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        Log.d("SIZEART", articles.size.toString())
    }

    override fun clickNews(it: Article) {
        Log.d("TAG", "clickNews() called with: it = $it")
        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        binding.imageViewAll.loadGlideNot(it.urlToImage)
        binding.dateNews.text = stringFromData(it.publishedAt).formatDateDay()
        binding.titleNews.text = it.title
        binding.descriptionNews.text = it.description
        // autor source url
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