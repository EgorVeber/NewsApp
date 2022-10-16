package ru.gb.veber.newsapi.view.topnews.pageritem

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
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
import ru.gb.veber.newsapi.view.activity.EventAddingBadges
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

    private var itemListener = object : RecyclerListener {

        override fun clickNews(article: Article) {
            presenter.clickNews(article)
        }

        override fun deleteFavorites(article: Article) {

        }
    }

    override fun emptyList() {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        binding.statusTextList.show()
    }

    private val newsAdapter = TopNewsAdapter(itemListener)

    private val presenter: TopNewsPresenter by moxyPresenter {
        TopNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()),
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
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
        presenter.loadNews(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL)
        presenter.getAccountSettings(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }

    override fun init() {
        binding.recyclerNews.adapter = newsAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())

        bSheetB = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }


//        binding.filterButton.setOnClickListener {
//            if (bSheetB.state == BottomSheetBehavior.STATE_EXPANDED) {
//                presenter.loadNewsCountry(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL,
//                    "us")
//            }
//            presenter.filterButtonClick()
//        }
    }

    @SuppressLint("SetTextI18n")
    override fun setSources(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
    }

    override fun changeNews(articleListHistory: MutableList<Article>) {
        newsAdapter.articles = articleListHistory
    }

    override fun sheetExpanded() {
        bSheetB.expanded()
    }

    override fun clickNews(article: Article) {

        hideFilter()
        with(binding) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpanDescription(article)
        }

        binding.descriptionNews.setOnClickListener { view ->
            presenter.openScreenWebView(article.url)
        }

        binding.imageFavorites.setOnClickListener {
            presenter.setOnClickImageFavorites(article)
        }
    }


    private fun setSpanDescription(article: Article) {
        SpannableStringBuilder(article.description).also { span ->
            span.setSpan(
                ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
                span.length - 1,
                span.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.descriptionNews.text = span
            span.removeSpan(span)
        }
    }

    override fun setLikeResourcesActive() {
        binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
    }

    override fun setLikeResourcesNegative() {
        binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
    }

    override fun addBadge() {
        (requireActivity() as EventAddingBadges).addBadge()
    }

    override fun removeBadge() {
        (requireActivity() as EventAddingBadges).removeBadge()
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
//        //Либо без задержки нее важно особо
//        Handler(Looper.getMainLooper()).postDelayed({
//            presenter.loadNews(arguments?.getString(BUNDLE_KEY) ?: CATEGORY_GENERAL,
//                arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
//        }, 2000)
    }

    override fun hideFavorites() {
        binding.imageFavorites.hide()
    }


    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    presenter.behaviorHide()
                    binding.filterButton.visibility = View.VISIBLE
                    binding.filterButton.setImageResource(R.drawable.filter_icon)
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }


    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}