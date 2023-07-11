package ru.gb.veber.newsapi.presentation.favorites

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.transition.TransitionManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.common.UiCoreDrawable
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.common.getAppComponent
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventShareLink
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.collapsed
import ru.gb.veber.ui_common.expanded
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_common.showText
import ru.gb.veber.ui_common.utils.BundleInt
import ru.gb.veber.ui_common.utils.BundleString
import ru.gb.veber.ui_common.utils.DateFormatter.toFormatDateDayMouthYearHoursMinutes
import ru.gb.veber.ui_core.databinding.FavotitesFragmentBinding
import ru.gb.veber.ui_core.extentions.loadPicForTitle

class FavoritesFragment :
    NewsFragment<FavotitesFragmentBinding, FavoritesViewModel>(FavotitesFragmentBinding::inflate),
    EventBehaviorToActivity {

    private var itemListener = object : TopNewsListener {

        override fun clickNews(articleUiModel: ArticleUiModel) {
            viewModel.clickNews(articleUiModel)
        }

        override fun deleteFavorites(articleUiModel: ArticleUiModel) {
            viewModel.deleteFavorites(articleUiModel)
        }

        override fun deleteHistory(articleUiModel: ArticleUiModel) {
            viewModel.deleteHistory(articleUiModel)
        }

        override fun clickGroupHistory(articleUiModel: ArticleUiModel) {
            viewModel.clickGroupHistory(articleUiModel)
        }

        override fun deleteGroupHistory(articleUiModel: ArticleUiModel) {
            viewModel.deleteGroupHistory(articleUiModel)
        }
    }

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private val historyAdapter = TopNewsAdapter(itemListener)

    private var accountID by BundleInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)
    private var tagPage by BundleString(BUNDLE_PAGE_KEY, FAVORITES)

    override fun getViewModelClass(): Class<FavoritesViewModel> = FavoritesViewModel::class.java
    override fun onInject() = getAppComponent().inject(this)

    override fun onInitView() {
        binding.likeRecycler.adapter = historyAdapter
        binding.likeRecycler.itemAnimator = null
        binding.likeRecycler.layoutManager = LinearLayoutManager(requireContext())
        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)
        binding.behaviorInclude.imageFavorites.hide()
    }

    override fun onObserveData() {
        viewModel.uiState.observe(viewLifecycleOwner, ::handleState)
    }

    override fun onViewInited() {
        viewModel.getAccountArticle(accountID, tagPage == FAVORITES )
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    private fun clickNews(articleModel: ArticleUiModel) {
        bSheetB.expanded()
        with(binding.behaviorInclude) {
            imageViewAll.loadPicForTitle(articleModel.urlToImage)
            dateNews.text = articleModel.publishedAt.toFormatDateDayMouthYearHoursMinutes()
            titleNews.text = articleModel.title
            authorText.text = articleModel.author
            sourceText.text = articleModel.sourceModel.name
            setSpanDescription(articleModel)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener {
            viewModel.openScreenWebView(articleModel.url)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(articleModel.url)
        }
    }

    private fun setSpanDescription(articleModel: ArticleUiModel) {
        SpannableStringBuilder(articleModel.description).also { span ->
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

    private fun handleState(state: FavoritesViewModel.FavoritesState) {
        when (state) {
            is FavoritesViewModel.FavoritesState.ClickNews -> {
                clickNews(state.articleModel)
            }

            is FavoritesViewModel.FavoritesState.SetSources -> {
                setSources(state.articleModels)
            }

            FavoritesViewModel.FavoritesState.Loading -> {
                loading()
            }

            FavoritesViewModel.FavoritesState.EmptyList -> {
                emptyList()
            }

            FavoritesViewModel.FavoritesState.NotAuthorized -> {
                notAuthorized()
            }

            FavoritesViewModel.FavoritesState.ErrorDeleteGroup -> {
                toastDeleteHistoryError()
            }
        }
    }

    private fun setSources(list: List<ArticleUiModel>) {
        TransitionManager.beginDelayedTransition(binding.root)
        historyAdapter.articleModels = list
        binding.likeRecycler.show()
    }

    private fun loading() {
        binding.statusTextLike.hide()
    }

    private fun notAuthorized() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(UiCoreStrings.not_authorized)
    }

    private fun emptyList() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(UiCoreStrings.empty_list)
        binding.likeRecycler.hide()
    }

    private fun toastDeleteHistoryError() {
        binding.root.showText(getString(UiCoreStrings.historyCanNotBeCleared))
    }

    companion object {
        private const val BUNDLE_PAGE_KEY = "BUNDLE_PAGE_KEY"
        fun getInstance(page: String, accountID: Int) = FavoritesFragment().apply {
            this.accountID = accountID
            this.tagPage = page
        }
    }
}
