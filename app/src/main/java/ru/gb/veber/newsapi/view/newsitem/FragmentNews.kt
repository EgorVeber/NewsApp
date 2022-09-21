package ru.gb.veber.newsapi.view.newsitem

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
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
import ru.gb.veber.newsapi.R
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

        binding.recyclerNews.addOnScrollListener(listener)

        binding.filterButton.setOnClickListener {
            if (bSheetB.state == BottomSheetBehavior.STATE_EXPANDED) {
                presenter.loadNewsCountry(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL,
                    "us")
            }
            presenter.filterButtonClick()
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
        binding.filterButton.visibility = View.INVISIBLE
        hideFilter()


        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        binding.imageViewAll.show()
        binding.titleNews.show()
        binding.dateNews.show()
        binding.descriptionNews.show()
        binding.imageViewAll.loadGlideNot(it.urlToImage)
        binding.dateNews.text = stringFromData(it.publishedAt).formatDateDay()
        binding.titleNews.text = it.title


        var spanableStringBuilder =
            SpannableStringBuilder(it.description)
        spanableStringBuilder.setSpan(
            ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
            spanableStringBuilder.length - 1,
            spanableStringBuilder.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        binding.descriptionNews.text = spanableStringBuilder

        binding.authorText.text = it.author
        binding.sourceText.text = it.source.name
        // autor source url
    }

    override fun showFilter() {
        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        binding.imageViewAll.hide()
        binding.titleNews.hide()
        binding.dateNews.hide()
        binding.descriptionNews.hide()
        binding.countrySpiner.show()
        binding.searchViewKeyWord.show()
        binding.filterTitle.show()
        binding.filterButton.setImageResource(R.drawable.check_icon)
    }

    override fun hideFilter() {
        bSheetB.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.countrySpiner.hide()
        binding.searchViewKeyWord.hide()
        binding.filterTitle.hide()
        binding.filterButton.setImageResource(R.drawable.filter_icon)
    }

    override fun behaviorHide() {
        bSheetB.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun visibilityFilterButton() {
        binding.filterButton.visibility = View.VISIBLE
    }


    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    presenter.behaviorHide()
                    binding.filterButton.visibility = View.VISIBLE
                    binding.filterButton.setImageResource(R.drawable.filter_icon)
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onBackPressedRouter(): Boolean {
//        if(bSheetB.state==BottomSheetBehavior.STATE_COLLAPSED){
//            return presenter.onBackPressedRouter(false)
//        }
        return presenter.onBackPressedRouter()
    }

    companion object {
        private const val BUNDLE_KEY = "BUNDLE_KEY"
        fun getInstance(category: String) = FragmentNews().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_KEY, category)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}