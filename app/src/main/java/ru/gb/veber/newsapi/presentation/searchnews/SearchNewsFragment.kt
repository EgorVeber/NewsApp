package ru.gb.veber.newsapi.presentation.searchnews

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.*
import ru.gb.veber.newsapi.common.utils.*
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchNewsFragmentBinding
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.presentation.activity.EventAddingBadges
import ru.gb.veber.newsapi.presentation.activity.EventShareLink
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

class SearchNewsFragment :
    NewsFragment<SearchNewsFragmentBinding, SearchNewsViewModel>(SearchNewsFragmentBinding::inflate),
    EventBehaviorToActivity {

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = TopNewsListener { article -> viewModel.clickNews(article) }

    private val newsAdapter = TopNewsAdapter(itemListener)

    private var historySelect by BundleHistorySelect(HISTORY_SELECT_BUNDLE)
    private var accountId by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    override fun getViewModelClass(): Class<SearchNewsViewModel> = SearchNewsViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)

        binding.allNewsRecycler.adapter = newsAdapter
        binding.allNewsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.backMainScreenImage.setOnClickListener { viewModel.exit() }

        binding.behaviorInclude.saveSources.setOnClickListener { viewModel.saveSources() }
    }

    override fun onObserveData() {
        viewModel.articleDataFlow.observeFlow(this) {
            clickNews(it)
        }

        viewModel.imageLikeFlow.observeFlow(this) { showLikeImageView ->
            if (showLikeImageView) setLikeResourcesActive() else setLikeResourcesNegative()
        }

        viewModel.saveSourcesFlow.observeFlow(this) { showSaveSources ->
            if (showSaveSources) showSaveSources() else hideSaveSources()
        }

        viewModel.showMessageFlow.observeFlow(this) { showMessage ->
            if (showMessage) successSaveSources()
        }

        viewModel.subscribe(accountId).observeFlow(this) { state ->
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

    override fun onStartAction() {
        viewModel.getAccountSettings(historySelect)
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
            imageViewAll.loadPicForTitle(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpan(article.description.toString())
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener { view ->
            viewModel.openScreenWebView(article.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
            viewModel.setOnClickImageFavorites(article)
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
        this.showSnackBar(getString(R.string.sourcesSaved))
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
