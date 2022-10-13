package ru.gb.veber.newsapi.view.search.searchnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AllNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.AllNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventAddingBadges
import ru.gb.veber.newsapi.view.topnews.pageritem.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.topnews.pageritem.RecyclerListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter


class AllNewsFragment : MvpAppCompatFragment(), AllNewsView, BackPressedListener,
    EventBehaviorToActivity {

    private var _binding: AllNewsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>


    private val presenter: AllNewsPresenter by moxyPresenter {
        AllNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()),
            arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT) ?: ACCOUNT_ID_DEFAULT,
            SourcesRepoImpl(App.instance.newsDb.sourcesDao()),
            AccountSourcesRepoImpl(App.instance.newsDb.accountSourcesDao()))
    }

    private var itemListener = object : RecyclerListener {

        override fun clickNews(article: Article) {
            presenter.clickNews(article)
        }

        override fun deleteFavorites(article: Article) {

        }
    }


    override fun emptyList() {
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
        binding.statusTextList.show()
    }

    private val newsAdapter = TopNewsAdapter(itemListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AllNewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        arguments?.let {
            presenter.getNews(
                it.getInt(ACCOUNT_ID),
                it.getString(KEY_WORD),
                it.getString(SEARCH_IN),
                it.getString(SORT_BY_KEY_WORD),
                it.getString(SORT_BY_SOURCES),
                it.getString(SOURCES_ID),
                it.getString(DATE_SOURCES),
                it.getString(SOURCES_NAME))
        }
        presenter.getAccountSettings(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
    }


    @SuppressLint("SetTextI18n")
    override fun setTitle(keyWord: String?, sourcesId: String?, s: String?, dateSources: String?) {
        binding.titleSearch.text = "$keyWord $sourcesId $s $dateSources"
    }

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    arguments?.let {
                        presenter.getNews(
                            it.getInt(ACCOUNT_ID),
                            it.getString(KEY_WORD),
                            it.getString(SEARCH_IN),
                            it.getString(SORT_BY_KEY_WORD),
                            it.getString(SORT_BY_SOURCES),
                            it.getString(SOURCES_ID),
                            it.getString(DATE_SOURCES),
                            it.getString(SOURCES_NAME))
                    }
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private fun initialization() {

        bSheetB = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }
        binding.allNewsRecycler.adapter = newsAdapter
        binding.allNewsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.backMainScreenImage.setOnClickListener {
            presenter.exit()
        }
    }

    override fun setNews(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
    }

    override fun loading() {
        binding.progressBarAllNews.show()
        binding.allNewsRecycler.hide()
    }

    override fun clickNews(article: Article) {

        if (article.isFavorites) {
            binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
        } else {
            binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
        }

        with(binding) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpan(article)
        }

        bSheetB.state = BottomSheetBehavior.STATE_EXPANDED
        presenter.saveArticle(article, arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)


        binding.descriptionNews.setOnClickListener { view ->
            presenter.openScreenWebView(article.url)
        }

        binding.imageFavorites.setOnClickListener { view ->
            if (article.isFavorites) {
                presenter.deleteFavorites(article)
                binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
                article.isFavorites = false
                (requireActivity() as EventAddingBadges).removeBadge()
            } else {
                Log.d("TAG", "else")
                presenter.saveArticleLike(article)
                binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
                article.isFavorites = true
                (requireActivity() as EventAddingBadges).addBadge()
            }
        }
    }

    override fun successInsertArticle() {
//        arguments?.let {
//            presenter.getNews(
//                it.getInt(ACCOUNT_ID),
//                it.getString(KEY_WORD),
//                it.getString(SEARCH_IN),
//                it.getString(SORT_BY_KEY_WORD),
//                it.getString(SORT_BY_SOURCES),
//                it.getString(SOURCES_ID),
//                it.getString(DATE_SOURCES),
//                it.getString(SOURCES_NAME))
//        }
    }

    private fun setSpan(article: Article) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(
            accountID: Int,
            keyWord: String?,
            searchIn: String?,
            sortByKeyWord: String?,
            sortBySources: String?,
            sourcesId: String?,
            dateSources: String?,
            sourcesName: String?,
        ): AllNewsFragment {
            return AllNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                    putString(KEY_WORD, keyWord)
                    putString(SEARCH_IN, searchIn)
                    putString(SORT_BY_KEY_WORD, sortByKeyWord)
                    putString(SORT_BY_SOURCES, sortBySources)
                    putString(SOURCES_NAME, sourcesName)
                    putString(SOURCES_ID, sourcesId)
                    putString(DATE_SOURCES, dateSources)
                }
            }
        }
    }

    override fun hideFavorites() {
        binding.imageFavorites.hide()
    }

    override fun getStateBehavior(): Int {
        Log.d("supportFragmentManager", "getStateBehavior() called ${bSheetB.state}")
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }
}