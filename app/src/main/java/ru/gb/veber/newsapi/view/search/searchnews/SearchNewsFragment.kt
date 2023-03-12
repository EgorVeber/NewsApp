package ru.gb.veber.newsapi.view.search.searchnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.HISTORY_SELECT_BUNDLE
import ru.gb.veber.newsapi.utils.extentions.BundleHistorySelect
import ru.gb.veber.newsapi.utils.extentions.BundleInt
import ru.gb.veber.newsapi.utils.extentions.collapsed
import ru.gb.veber.newsapi.utils.extentions.expanded
import ru.gb.veber.newsapi.utils.extentions.formatDateDay
import ru.gb.veber.newsapi.utils.extentions.hide
import ru.gb.veber.newsapi.utils.extentions.loadGlideNot
import ru.gb.veber.newsapi.utils.extentions.observeFlow
import ru.gb.veber.newsapi.utils.extentions.show
import ru.gb.veber.newsapi.utils.extentions.showSnackBarError
import ru.gb.veber.newsapi.utils.extentions.stringFromData
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventAddingBadges
import ru.gb.veber.newsapi.view.activity.EventShareLink
import ru.gb.veber.newsapi.view.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener
import javax.inject.Inject


class SearchNewsFragment : Fragment(), BackPressedListener,
    EventBehaviorToActivity {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val searchNewsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SearchNewsViewModel::class.java]
    }

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = TopNewsListener { article -> searchNewsViewModel.clickNews(article) }

    private val newsAdapter = TopNewsAdapter(itemListener)

    private var historySelect by BundleHistorySelect(HISTORY_SELECT_BUNDLE)
    private var accountId by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SearchNewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        initialization()
        searchNewsViewModel.getAccountSettings(historySelect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return searchNewsViewModel.onBackPressedRouter()
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    private fun initialization() {
        initView()
        initViewModel()
    }

    private fun initView() {
        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)

        binding.allNewsRecycler.adapter = newsAdapter
        binding.allNewsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.backMainScreenImage.setOnClickListener { searchNewsViewModel.exit() }

        binding.behaviorInclude.saveSources.setOnClickListener { searchNewsViewModel.saveSources() }
    }

    private fun initViewModel() {
        searchNewsViewModel.articleDataFlow.observeFlow(this) {
            clickNews(it)
        }

        searchNewsViewModel.imageLikeFlow.observeFlow(this) { showLikeImageView ->
            if (showLikeImageView) setLikeResourcesActive() else setLikeResourcesNegative()
        }

        searchNewsViewModel.saveSourcesFlow.observeFlow(this) { showSaveSources ->
            if (showSaveSources) showSaveSources() else hideSaveSources()
        }

        searchNewsViewModel.showMessageFlow.observeFlow(this) { showMessage ->
            if (showMessage) successSaveSources()
        }

        searchNewsViewModel.subscribe(accountId).observeFlow(this) { state ->
            when (state) {
                is SearchNewsViewModel.SearchNewsState.SetNews -> {
                    setNews(state.list)
                }
                is SearchNewsViewModel.SearchNewsState.ChangeNews -> {
                    changeNews(state.articleListHistory)
                }
                is SearchNewsViewModel.SearchNewsState.SetTitle -> {
                    setTitle(state.historySelect)
                }
                SearchNewsViewModel.SearchNewsState.EmptyList -> {
                    emptyList()
                }
                SearchNewsViewModel.SearchNewsState.HideFavorites -> {
                    hideFavorites()
                }

                SearchNewsViewModel.SearchNewsState.AddBadge -> {
                    addBadge()
                }
                SearchNewsViewModel.SearchNewsState.RemoveBadge -> {
                    removeBadge()
                }
                SearchNewsViewModel.SearchNewsState.SheetExpanded -> {
                    sheetExpanded()
                }
                SearchNewsViewModel.SearchNewsState.StartedState -> {}
                SearchNewsViewModel.SearchNewsState.HideProgress -> {
                    hideProgress()
                }
            }
        }
    }

    private fun setTitle(historySelect: HistorySelect) {
        val keyWord = historySelect.keyWord
        val sourcesId = historySelect.sourcesName
        val sortType =
            if (!historySelect.keyWord.isNullOrEmpty()) historySelect.sortByKeyWord
            else historySelect.sortBySources
        val dateSources = historySelect.dateSources
        val text = "$keyWord $sourcesId $sortType $dateSources"

        binding.titleSearch.text = text
    }

    private fun setNews(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
    }

    private fun hideProgress() {
        binding.progressBarAllNews.hide()
    }

    private fun changeNews(articleListHistory: List<Article>) {
        newsAdapter.articles = articleListHistory
    }

    private fun emptyList() {
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
        binding.statusTextList.show()
    }

    private fun clickNews(article: Article) {

        with(binding.behaviorInclude) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpan(article.description.toString())
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener { view ->
            searchNewsViewModel.openScreenWebView(article.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
            searchNewsViewModel.setOnClickImageFavorites(article)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(article.url)
        }

    }

    private fun showSaveSources() {
        binding.behaviorInclude.saveSources.show()
    }

    private fun hideSaveSources() {
        binding.behaviorInclude.saveSources.hide()
    }

    private fun sheetExpanded() {
        bSheetB.expanded()
    }

    private fun setLikeResourcesActive() {
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
    }

    private fun setLikeResourcesNegative() {
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
    }

    private fun addBadge() {
        (requireActivity() as EventAddingBadges).addBadge()
    }

    private fun removeBadge() {
        (requireActivity() as EventAddingBadges).removeBadge()
    }

    private fun successSaveSources() {
        binding.root.showSnackBarError(getString(R.string.sourcesSaved), "", {})
    }

    private fun setSpan(description: String) {
        SpannableStringBuilder(description).also { span ->
            span.setSpan(
                ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
                span.length - 1,
                span.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.behaviorInclude.descriptionNews.text = span
            span.removeSpan(span)
        }
    }

    private fun hideFavorites() {
        binding.behaviorInclude.imageFavorites.hide()
    }

    companion object {
        fun getInstance(
            accountId: Int,
            historySelect: HistorySelect,
        ): SearchNewsFragment {
            return SearchNewsFragment().apply {
                this.accountId = accountId
                this.historySelect = historySelect
            }
        }
    }
}
