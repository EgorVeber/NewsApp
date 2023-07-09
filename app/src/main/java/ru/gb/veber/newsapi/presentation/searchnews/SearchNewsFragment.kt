package ru.gb.veber.newsapi.presentation.searchnews

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.common.UiCoreDrawable
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventAddingBadges
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventShareLink
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.collapsed
import ru.gb.veber.ui_common.coroutine.observeFlow
import ru.gb.veber.ui_common.expanded
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_common.utils.BundleInt
import ru.gb.veber.ui_common.utils.DateFormatter.toFormatDateDayMouthYearHoursMinutes
import ru.gb.veber.ui_core.databinding.SearchNewsFragmentBinding
import ru.gb.veber.ui_core.extentions.loadPicForTitle
import ru.gb.veber.ui_core.extentions.showSnackBar

class SearchNewsFragment :
    NewsFragment<SearchNewsFragmentBinding, SearchNewsViewModel>(SearchNewsFragmentBinding::inflate),
    EventBehaviorToActivity {

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = TopNewsListener { article -> viewModel.clickNews(article) }

    private val newsAdapter = TopNewsAdapter(itemListener)

    private var historySelect by BundleHistorySelect(HISTORY_SELECT_BUNDLE)
    private var accountId by BundleInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)

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
        viewModel.articleModelDataFlow.observeFlow(this) {
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
                    changeNews(state.articleModelListHistory)
                }

                is SearchNewsViewModel.SearchNewsState.SetTitle -> {
                    setTitle(state.historySelectModel)
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

    override fun onViewInited() {
        viewModel.getAccountSettings(historySelect)
    }

    private fun setTitle(historySelectModel: HistorySelectModel) {
        val keyWord = historySelectModel.keyWord
        val sourcesId = historySelectModel.sourcesName
        val sortType =
            if (!historySelectModel.keyWord.isNullOrEmpty()) historySelectModel.sortByKeyWord
            else historySelectModel.sortBySources
        val dateSources = historySelectModel.dateSources
        val text = "$keyWord $sourcesId $sortType $dateSources"

        binding.titleSearch.text = text
    }

    private fun setNews(articleModels: List<ArticleUiModel>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articleModels = articleModels
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
    }

    private fun hideProgress() {
        binding.progressBarAllNews.hide()
    }

    private fun changeNews(articleModelListHistory: List<ArticleUiModel>) {
        newsAdapter.articleModels = articleModelListHistory
    }

    private fun emptyList() {
        binding.progressBarAllNews.hide()
        binding.allNewsRecycler.show()
        binding.statusTextList.show()
    }

    private fun clickNews(articleModel: ArticleUiModel) {

        with(binding.behaviorInclude) {
            imageViewAll.loadPicForTitle(articleModel.urlToImage)
            dateNews.text = articleModel.publishedAt.toFormatDateDayMouthYearHoursMinutes()
            titleNews.text = articleModel.title
            authorText.text = articleModel.author
            sourceText.text = articleModel.sourceModel.name
            setSpan(articleModel.description)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener {
            viewModel.openScreenWebView(articleModel.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
            viewModel.setOnClickImageFavorites(articleModel)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(articleModel.url)
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
        binding.behaviorInclude.imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36_active)
    }

    private fun setLikeResourcesNegative() {
        binding.behaviorInclude.imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36)
    }

    private fun addBadge() {
        (requireActivity() as EventAddingBadges).addBadge()
    }

    private fun removeBadge() {
        (requireActivity() as EventAddingBadges).removeBadge()
    }

    private fun successSaveSources() {
        this.showSnackBar(getString(UiCoreStrings.sourcesSaved))
    }

    private fun setSpan(description: String) {
        SpannableStringBuilder(description).also { span ->
            span.setSpan(
                ImageSpan(requireContext(), UiCoreDrawable.ic_baseline_open_in_new_24),
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
        private const val HISTORY_SELECT_BUNDLE = "HISTORY_SELECT_BUNDLE"

        fun getInstance(
            accountId: Int,
            historySelectModel: HistorySelectModel,
        ): SearchNewsFragment {
            return SearchNewsFragment().apply {
                this.accountId = accountId
                this.historySelect = historySelectModel
            }
        }
    }
}
