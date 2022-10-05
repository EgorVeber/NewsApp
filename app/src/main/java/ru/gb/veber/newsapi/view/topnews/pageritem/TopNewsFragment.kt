package ru.gb.veber.newsapi.view.topnews.pageritem

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.TopNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.TopNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_GENERAL


interface EventBehaviorToActivity {
    fun getStateBehavior(): Int
    fun setStateBehavior()
}

class TopNewsFragment : MvpAppCompatFragment(), TopNewsView, BackPressedListener,
    EventBehaviorToActivity {

    private var _binding: TopNewsFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>


    private val newsAdapter = TopNewsAdapter {
        presenter.clickNews(it)
        presenter.saveArticle(it, arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }

    private val presenter: TopNewsPresenter by moxyPresenter {
        TopNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router, ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()))
    }

    companion object {
        private const val BUNDLE_KEY = "BUNDLE_KEY"
        fun getInstance(category: String, accountId: Int) = TopNewsFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_KEY, category)
                putInt(ACCOUNT_ID, accountId)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TopNewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadNews(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL,
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        presenter.getAccountSettings(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
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

//        var itemAnimator = binding.recyclerNews.itemAnimator
//        if(itemAnimator is DefaultItemAnimator){
//            itemAnimator.supportsChangeAnimations=false
//        }

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
        Log.d("setSources", "size " + articles.size.toString())
        Log.d("setSources", articles.filter { it.isHistory }.size.toString())
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
    }

    override fun clickNews(article: Article) {
        if (article.isFavorites) {
            binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        } else {
            binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
        }


        binding.filterButton.visibility = View.INVISIBLE
        hideFilter()
        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        binding.imageViewAll.show()
        binding.titleNews.show()
        binding.dateNews.show()
        binding.authorText.show()
        binding.imageFavorites.show()
        binding.descriptionNews.show()
        binding.imageViewAll.loadGlideNot(article.urlToImage)
        binding.dateNews.text = stringFromData(article.publishedAt).formatDateDay()
        binding.titleNews.text = article.title


        var spanableStringBuilder =
            SpannableStringBuilder(article.description)
        spanableStringBuilder.setSpan(
            ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
            spanableStringBuilder.length - 1,
            spanableStringBuilder.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        binding.descriptionNews.text = spanableStringBuilder
        binding.authorText.text = article.author
        binding.sourceText.text = article.source.name
        spanableStringBuilder.removeSpan(spanableStringBuilder)



        binding.descriptionNews.setOnClickListener { view ->
            presenter.openScreenWebView(article.url)
        }

        binding.imageFavorites.setOnClickListener { view ->
            if (article.isFavorites) {
                Log.d("TAG", "article.isFavorites")
                presenter.deleteFavorites(article)
                binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
                article.isFavorites = false
            } else {
                Log.d("TAG", "else")
                presenter.saveArticleLike(article,
                    arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
                binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
                article.isFavorites = true
            }
        }
    }

    override fun showFilter() {
        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        binding.imageViewAll.hide()
        binding.titleNews.hide()
        binding.authorText.hide()
        binding.imageFavorites.hide()
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

    override fun successInsertArticle() {

    }


    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    presenter.behaviorHide()
                    binding.filterButton.visibility = View.VISIBLE
                    binding.filterButton.setImageResource(R.drawable.filter_icon)
                    presenter.loadNews(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL,
                        arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onBackPressedRouter(): Boolean {
        Log.d("Back", "onBackPressedRouter() FragmentNew override")
        return presenter.onBackPressedRouter()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("@@@onSaveInstanceState", "onSaveInstanceState() called with: outState = $outState")
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        Log.d("@@@setMenuVisibility", "setMenuVisibility() called with: menuVisible = $menuVisible")
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }
}