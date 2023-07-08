package ru.gb.veber.newsapi.presentation.favorites

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.transition.TransitionManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.DateFormatter.toFormatDateDayMouthYearHoursMinutes
import ru.gb.veber.newsapi.common.extentions.collapsed
import ru.gb.veber.newsapi.common.extentions.expanded
import ru.gb.veber.newsapi.common.extentions.getAppComponent
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadPicForTitle
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.extentions.showText
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.BundleString
import ru.gb.veber.newsapi.common.utils.PAGE
import ru.gb.veber.newsapi.databinding.FavotitesFragmentBinding
import ru.gb.veber.newsapi.presentation.activity.EventShareLink
import ru.gb.veber.newsapi.presentation.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener

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

    private var accountID by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    private var tagPage by BundleString(PAGE, FAVORITES)

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
        viewModel.getAccountArticle(accountID, tagPage)
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
                ImageSpan(requireContext(), R.drawable.ic_baseline_open_in_new_24),
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
        if (list.isEmpty()) {
            emptyList()
        }
        historyAdapter.articleModels = list
        binding.likeRecycler.show()
    }

    private fun loading() {
        binding.statusTextLike.hide()
    }

    private fun notAuthorized() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(R.string.not_authorized)
    }

    private fun emptyList() {
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(R.string.empty_list)
    }

    private fun toastDeleteHistoryError() {
        binding.root.showText(getString(R.string.historyCanNotBeCleared))
    }

    companion object {
        fun getInstance(page: String, accountID: Int) = FavoritesFragment().apply {
            this.accountID = accountID
            this.tagPage = page
        }
    }
}
