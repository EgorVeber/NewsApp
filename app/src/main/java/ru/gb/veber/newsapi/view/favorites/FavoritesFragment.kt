package ru.gb.veber.newsapi.view.favorites

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.transition.TransitionManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.collapsed
import ru.gb.veber.newsapi.common.extentions.expanded
import ru.gb.veber.newsapi.common.extentions.formatDateDay
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.loadGlideNot
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.extentions.showText
import ru.gb.veber.newsapi.common.extentions.stringFromData
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.BundleString
import ru.gb.veber.newsapi.common.utils.PAGE
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavotitesFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.PAGE
import ru.gb.veber.newsapi.utils.extentions.collapsed
import ru.gb.veber.newsapi.utils.extentions.expanded
import ru.gb.veber.newsapi.utils.extentions.showText
import ru.gb.veber.newsapi.utils.extentions.loadGlideNot
import ru.gb.veber.newsapi.utils.extentions.stringFromData
import ru.gb.veber.newsapi.utils.extentions.formatDateDay
import ru.gb.veber.newsapi.utils.extentions.hide
import ru.gb.veber.newsapi.utils.extentions.show
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.domain.models.Article
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.newsapi.view.activity.EventShareLink
import ru.gb.veber.newsapi.view.favorites.viewpager.FavoritesViewPagerAdapter.Companion.FAVORITES

class FavoritesFragment :
    NewsFragment<FavotitesFragmentBinding, FavoritesViewModel>(FavotitesFragmentBinding::inflate),
    EventBehaviorToActivity {

    private var itemListener = object : TopNewsListener {

        override fun clickNews(article: Article) {
            viewModel.clickNews(article)
        }

        override fun deleteFavorites(article: Article) {
            viewModel.deleteFavorites(article)
        }

        override fun deleteHistory(article: Article) {
            viewModel.deleteHistory(article)
        }

        override fun clickGroupHistory(article: Article) {
            viewModel.clickGroupHistory(article)
        }

        override fun deleteGroupHistory(article: Article) {
            viewModel.deleteGroupHistory(article)
        }
    }

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private val historyAdapter = TopNewsAdapter(itemListener)

    private var accountID by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    private var tagPage by BundleString(PAGE, FAVORITES)

    override fun getViewModelClass(): Class<FavoritesViewModel> {
        return FavoritesViewModel::class.java
    }

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

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

    override fun onStartAction() {
        viewModel.getAccountArticle(accountID, tagPage)
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    private fun clickNews(article: Article) {
        bSheetB.expanded()
        with(binding.behaviorInclude) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpanDescription(article)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener {
            viewModel.openScreenWebView(article.url)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(article.url)
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
            binding.behaviorInclude.descriptionNews.text = span
            span.removeSpan(span)
        }
    }

    private fun handleState(state: FavoritesViewModel.FavoritesState) {
        when (state) {
            is FavoritesViewModel.FavoritesState.ClickNews -> {
                clickNews(state.article)
            }
            is FavoritesViewModel.FavoritesState.SetSources -> {
                setSources(state.articles)
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
            FavoritesViewModel.FavoritesState.ErrorDeleteGroup -> {
                toastDeleteHistoryError()
            }
        }
    }

    private fun init() {
        binding.likeRecycler.adapter = historyAdapter
        binding.likeRecycler.itemAnimator = null
        binding.likeRecycler.layoutManager = LinearLayoutManager(requireContext())
        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)
        binding.behaviorInclude.imageFavorites.hide()
    }

    private fun setSources(list: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        if (list.isEmpty()) {
            emptyList()
        }
        historyAdapter.articles = list
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
