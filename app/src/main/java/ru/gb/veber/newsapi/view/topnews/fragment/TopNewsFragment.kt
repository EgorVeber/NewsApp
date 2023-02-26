package ru.gb.veber.newsapi.view.topnews.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.TopNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.DURATION_ERROR_INPUT
import ru.gb.veber.newsapi.utils.extentions.collapsed
import ru.gb.veber.newsapi.utils.extentions.expanded
import ru.gb.veber.newsapi.utils.extentions.formatDateDay
import ru.gb.veber.newsapi.utils.extentions.hide
import ru.gb.veber.newsapi.utils.extentions.hideKeyboard
import ru.gb.veber.newsapi.utils.extentions.loadGlideNot
import ru.gb.veber.newsapi.utils.extentions.show
import ru.gb.veber.newsapi.utils.extentions.stringFromData
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventAddingBadges
import ru.gb.veber.newsapi.view.activity.EventShareLink
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener
import ru.gb.veber.newsapi.view.topnews.viewpager.EventTopNews
import ru.gb.veber.newsapi.view.topnews.viewpager.TopNewsViewPagerAdapter.Companion.CATEGORY_GENERAL
import javax.inject.Inject


class TopNewsFragment : Fragment(), BackPressedListener, EventBehaviorToActivity {

    private var _binding: TopNewsFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val topNewsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TopNewsViewModel::class.java]
    }

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = TopNewsListener { article -> topNewsViewModel.clickNews(article) }
    private val newsAdapter = TopNewsAdapter(itemListener)

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    topNewsViewModel.behaviorCollapsed()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { _, _, _, _ ->
        binding.countryAutoComplete.hideKeyboard()
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
        App.instance.appComponent.inject(this)
        initialization()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return topNewsViewModel.onBackPressedRouter()
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }

    private fun initialization() {
        val accountId = arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT
        val categoryKey = arguments?.getString(CATEGORY_KEY) ?: CATEGORY_GENERAL
        initViewModel(accountId = accountId, categoryKey = categoryKey)
        initView()
    }

    private fun initViewModel(accountId: Int, categoryKey: String) {
        topNewsViewModel.subscribe(accountId = accountId, categoryKey = categoryKey)
            .observe(viewLifecycleOwner) { state ->
                when (state) {
                    is TopNewsViewModel.TopNewsState.UpdateListNews -> {
                        updateListNews(newListNews = state.articleListHistory)
                    }
                    is TopNewsViewModel.TopNewsState.ClickNews -> {
                        clickNews(article = state.article)
                    }
                    is TopNewsViewModel.TopNewsState.SetNews -> {
                        setNews(listNews = state.articles)
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

    private fun initView() {
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
            topNewsViewModel.filterButtonClick(country = binding.countryAutoComplete.text.toString())
        }

        binding.closeFilter.setOnClickListener {
            topNewsViewModel.closeFilter()
        }

        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)
        bSheetB.addBottomSheetCallback(callBackBehavior)
    }

    private fun setNews(listNews: List<Article>) {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = listNews
        topNewsViewModel.getCountry()
    }

    private fun updateListNews(newListNews: MutableList<Article>) {
        newsAdapter.articles = newListNews
    }

    private fun clickNews(article: Article) {
        with(binding.behaviorInclude) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpanDescription(article)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener {
            topNewsViewModel.openScreenWebView(article.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
            topNewsViewModel.clickImageFavorites(article)
        }

        binding.behaviorInclude.imageShare.setOnClickListener {
            (requireActivity() as EventShareLink).shareLink(article.url)
        }
    }

    private fun setCountry(countryList: List<String>, startIndex: Int) {
        binding.countryAutoComplete.setAdapter(ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, countryList))
        binding.countryAutoComplete.setText(binding.countryAutoComplete.adapter.getItem(startIndex)
            .toString(), false)
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

    private fun showFilterHideRecycler() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_FILTER_HIDE
            transition.addTransition(Fade(Fade.IN))
            transition.addTransition(Fade(Fade.OUT))
            TransitionManager.beginDelayedTransition(binding.root, transition)

            binding.recyclerNews.alpha = ALFA_FILTER_HIDE
            binding.filterButton.setImageResource(R.drawable.check_icon)
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
        binding.filterButton.setImageResource(R.drawable.filter_icon)
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
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
    }

    private fun favoritesImageViewSetDislike() {
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
    }

    private fun bottomSheetExpanded() {
        bSheetB.expanded()
    }

    private fun errorLoadNews() {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        binding.statusTextList.show()
        binding.statusTextList.text = getString(R.string.error_load_news)
    }


    private fun errorSelectCountry() {
        binding.countryTextInput.error = getString(R.string.errorCountryNotSelected)
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
                    putInt(ACCOUNT_ID, accountId)
                }
            }
    }

}