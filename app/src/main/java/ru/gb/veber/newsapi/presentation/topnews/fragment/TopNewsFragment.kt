package ru.gb.veber.newsapi.presentation.topnews.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.common.UiCoreDrawable
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventAddingBadges
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventShareLink
import ru.gb.veber.newsapi.presentation.base.NewsFragment
import ru.gb.veber.newsapi.presentation.models.ArticleUiModel
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.presentation.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.newsapi.presentation.topnews.viewpager.EventTopNews
import ru.gb.veber.newsapi.presentation.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_GENERAL
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.DURATION_ERROR_INPUT
import ru.gb.veber.ui_common.collapsed
import ru.gb.veber.ui_common.expanded
import ru.gb.veber.ui_common.hide
import ru.gb.veber.ui_common.hideKeyboard
import ru.gb.veber.ui_common.show
import ru.gb.veber.ui_common.utils.DateFormatter.toFormatDateDayMouthYearHoursMinutes
import ru.gb.veber.ui_core.databinding.TopNewsFragmentBinding
import ru.gb.veber.ui_core.extentions.loadPicForTitle

class TopNewsFragment :
    NewsFragment<TopNewsFragmentBinding, TopNewsViewModel>(TopNewsFragmentBinding::inflate),
    EventBehaviorToActivity {

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = TopNewsListener { article -> viewModel.clickNews(article) }
    private val newsAdapter = TopNewsAdapter(itemListener)

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    viewModel.behaviorCollapsed()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { _, _, _, _ ->
        binding.countryAutoComplete.hideKeyboard()
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    override fun getViewModelClass() = TopNewsViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        with(binding.countryAutoComplete) {
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
        }

        binding.recyclerNews.adapter = newsAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())

        binding.filterButton.setOnClickListener {
            viewModel.filterButtonClick(country = binding.countryAutoComplete.text.toString())
        }

        binding.closeFilter.setOnClickListener {
            viewModel.closeFilter(country = binding.countryAutoComplete.text.toString())
        }


        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)
        bSheetB.addBottomSheetCallback(callBackBehavior)
    }


    override fun onObserveData() {
        val accountId = arguments?.getInt(BUNDLE_ACCOUNT_ID_KEY) ?: ACCOUNT_ID_DEFAULT
        val categoryKey = arguments?.getString(CATEGORY_KEY) ?: CATEGORY_GENERAL
        viewModel.subscribe(accountId = accountId, categoryKey = categoryKey)
            .observe(viewLifecycleOwner) { state ->
                when (state) {
                    is TopNewsViewModel.TopNewsState.UpdateListNews -> {
                        updateListNews(newListNews = state.articleModelListHistory)
                    }

                    is TopNewsViewModel.TopNewsState.ClickNews -> {
                        clickNews(articleUiModel = state.articleModel)
                    }

                    is TopNewsViewModel.TopNewsState.SetNews -> {
                        setNews(listNews = state.articleModels)
                    }

                    is TopNewsViewModel.TopNewsState.SetCountry -> {
                        setCountry(countryList = state.listCountry, startIndex = state.index)
                    }

                    TopNewsViewModel.TopNewsState.BottomSheetExpanded -> {
                        bottomSheetExpanded()
                    }

                    TopNewsViewModel.TopNewsState.EmptyListLoadNews -> {
                        emptyListLoadNews()
                    }

                    TopNewsViewModel.TopNewsState.ErrorLoadNews -> {
                        errorLoadNews()
                    }

                    TopNewsViewModel.TopNewsState.ErrorSelectCountry -> {
                        errorSelectCountry()
                    }

                    TopNewsViewModel.TopNewsState.EventNavigationBarAddBadgeFavorites -> {
                        eventNavigationBarAddBadgeFavorites()
                    }

                    TopNewsViewModel.TopNewsState.EventNavigationBarRemoveBadgeFavorites -> {
                        eventNavigationBarRemoveBadgeFavorites()
                    }

                    TopNewsViewModel.TopNewsState.EventUpdateViewPager -> {
                        eventUpdateViewPager()
                    }

                    TopNewsViewModel.TopNewsState.FavoritesImageViewSetDislike -> {
                        favoritesImageViewSetDislike()
                    }

                    TopNewsViewModel.TopNewsState.FavoritesImageViewSetLike -> {
                        favoritesImageViewSetLike()
                    }

                    TopNewsViewModel.TopNewsState.HideFavoritesImageView -> {
                        hideFavoritesImageView()
                    }

                    TopNewsViewModel.TopNewsState.HideFilterButton -> {
                        hideFilterButton()
                    }

                    TopNewsViewModel.TopNewsState.HideFilterShowRecycler -> {
                        hideFilterShowRecycler()
                    }

                    TopNewsViewModel.TopNewsState.ShowFilterButton -> {
                        showFilterButton()
                    }

                    TopNewsViewModel.TopNewsState.ShowFilterHideRecycler -> {
                        showFilterHideRecycler()
                    }
                }
            }
    }

    private fun setNews(listNews: List<ArticleUiModel>) {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articleModels = listNews
        viewModel.getCountry()
    }

    private fun updateListNews(newListNews: MutableList<ArticleUiModel>) {
        newsAdapter.articleModels = newListNews
    }

    private fun clickNews(articleUiModel: ArticleUiModel) {
        with(binding.behaviorInclude) {
            imageViewAll.loadPicForTitle(articleUiModel.urlToImage)
            dateNews.text = articleUiModel.publishedAt.toFormatDateDayMouthYearHoursMinutes()
            titleNews.text = articleUiModel.title
            authorText.text = articleUiModel.author
            sourceText.text = articleUiModel.sourceModel.name
            setSpanDescription(articleUiModel)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener {
            viewModel.openScreenWebView(articleUiModel.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
            viewModel.clickImageFavorites(articleUiModel)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(articleUiModel.url)
        }
    }

    private fun setCountry(countryList: List<String>, startIndex: Int) {
        binding.countryAutoComplete.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, countryList
            )
        )
        binding.countryAutoComplete.setText(
            binding.countryAutoComplete.adapter.getItem(startIndex)
                .toString(), false
        )
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

    private fun showFilterHideRecycler() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_FILTER_HIDE
            transition.addTransition(Fade(Fade.IN))
            transition.addTransition(Fade(Fade.OUT))
            TransitionManager.beginDelayedTransition(binding.root, transition)

            binding.recyclerNews.alpha = ALFA_FILTER_HIDE
            binding.filterButton.setImageResource(UiCoreDrawable.check_icon)
            binding.countryTextInput.show()
            binding.closeFilter.show()
        }
    }

    private fun showFilterButton() {
        binding.filterButton.show()
    }

    private fun hideFilterShowRecycler() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.recyclerNews.alpha = ALFA_FILTER_SHOW
        binding.filterButton.setImageResource(UiCoreDrawable.filter_icon)
        binding.countryTextInput.hide()
        binding.closeFilter.hide()
    }

    private fun hideFavoritesImageView() {
        binding.behaviorInclude.imageFavorites.hide()
    }

    private fun hideFilterButton() {
        binding.filterButton.hide()
    }

    private fun favoritesImageViewSetLike() {
        binding.behaviorInclude.imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36_active)
    }

    private fun favoritesImageViewSetDislike() {
        binding.behaviorInclude.imageFavorites.setImageResource(UiCoreDrawable.ic_favorite_36)
    }

    private fun bottomSheetExpanded() {
        bSheetB.expanded()
    }

    private fun errorLoadNews() {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        binding.statusTextList.show()
        binding.statusTextList.text = getString(UiCoreStrings.error_load_news)
    }


    private fun errorSelectCountry() {
        binding.countryTextInput.error = getString(UiCoreStrings.errorCountryNotSelected)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.countryTextInput.error = null
        }, DURATION_ERROR_INPUT)
    }

    private fun emptyListLoadNews() {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        binding.statusTextList.show()
    }

    private fun eventUpdateViewPager() {
        requireActivity().supportFragmentManager.fragments.forEach {
            if (it is EventTopNews) {
                (it as EventTopNews).updateViewPager()
            }
        }
    }

    private fun eventNavigationBarAddBadgeFavorites() {
        (requireActivity() as EventAddingBadges).addBadge()
    }

    private fun eventNavigationBarRemoveBadgeFavorites() {
        (requireActivity() as EventAddingBadges).removeBadge()
    }


    companion object {
        private const val CATEGORY_KEY = "CATEGORY_KEY"
        const val ALFA_FILTER_SHOW = 1F
        const val ALFA_FILTER_HIDE = 0F
        const val DURATION_FILTER_HIDE = 500L
        fun getInstance(category: String, accountId: Int) =
            TopNewsFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY_KEY, category)
                    putInt(BUNDLE_ACCOUNT_ID_KEY, accountId)
                }
            }
    }
}
