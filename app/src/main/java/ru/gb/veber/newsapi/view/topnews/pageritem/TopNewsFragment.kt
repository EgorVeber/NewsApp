package ru.gb.veber.newsapi.view.topnews.pageritem

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.TopNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.network.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.CountryRepoImpl
import ru.gb.veber.newsapi.presenter.TopNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventAddingBadges
import ru.gb.veber.newsapi.view.topnews.viewpager.EventTopNews
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

        override fun deleteFavorites(article: Article) {}
    }


    private val newsAdapter = TopNewsAdapter(itemListener)

    private val presenter: TopNewsPresenter by moxyPresenter {
        TopNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()),
            AccountRepoImpl(App.instance.newsDb.accountsDao()),
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT,
            CountryRepoImpl(App.instance.newsDb.countryDao()), SharedPreferenceAccount())
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
        presenter.loadNews(arguments?.getString(CATEGORY_KEY) ?: CATEGORY_GENERAL)
        presenter.getCountry()
        presenter.getAccountSettings()
    }

    override fun init() {
        binding.recyclerNews.adapter = newsAdapter
        binding.recyclerNews.itemAnimator = null
        binding.recyclerNews.layoutManager = LinearLayoutManager(requireContext())

        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }

        with(binding.countryAutoComplete) {
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
        }

        binding.filterButton.setOnClickListener {
            presenter.filterButtonClick(binding.countryAutoComplete.text.toString())
        }

        binding.cancelFilter.setOnClickListener {
            presenter.cancelButtonClick()
        }
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

        with(binding.behaviorInclude) {
            imageViewAll.loadGlideNot(article.urlToImage)
            dateNews.text = stringFromData(article.publishedAt).formatDateDay()
            titleNews.text = article.title
            authorText.text = article.author
            sourceText.text = article.source.name
            setSpanDescription(article)
        }

        binding.behaviorInclude.descriptionNews.setOnClickListener { view ->
            presenter.openScreenWebView(article.url)
        }

        binding.behaviorInclude.imageFavorites.setOnClickListener {
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
            binding.behaviorInclude.descriptionNews.text = span
            span.removeSpan(span)
        }
    }

    override fun setLikeResourcesActive() {
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
    }

    override fun setLikeResourcesNegative() {
        binding.behaviorInclude.imageFavorites.setImageResource(R.drawable.ic_favorite_36)
    }

    override fun addBadge() {
        (requireActivity() as EventAddingBadges).addBadge()
    }

    override fun removeBadge() {
        (requireActivity() as EventAddingBadges).removeBadge()
    }

    override fun showFilter() {
        binding.filterButton.show()
    }

    override fun hideCancelButton() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.cancelFilter.hide()
    }

    override fun setAlfaCancel() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.recyclerNews.alpha = ALFA_FILTER_SHOW
    }

    override fun hideCountryList() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.countryTextInput.hide()
    }

    override fun setImageFilterButtonCancel() {
        binding.filterButton.setImageResource(R.drawable.filter_icon)
    }


    override fun behaviorHide() {
        bSheetB.collapsed()
    }


    override fun visibilityFilterButton() {
        binding.filterButton.visibility = View.VISIBLE
    }


    override fun hideFavorites() {
        binding.behaviorInclude.imageFavorites.hide()
    }

    override fun hideFilter() {
        binding.filterButton.hide()
    }

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    presenter.behaviorCollapsed()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { _, _, _, _ ->
        binding.countryAutoComplete.hideKeyboard()
    }

    override fun setCountry(country: List<String>, index: Int) {
        binding.countryAutoComplete.setAdapter(ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, country))
        binding.countryAutoComplete.setText(binding.countryAutoComplete.adapter.getItem(index)
            .toString(), false);
    }


    override fun emptyList() {
        binding.progressBarTopNews.hide()
        binding.recyclerNews.show()
        binding.statusTextList.show()
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

    override fun fadeRecyclerShowCountry() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_FILTER_HIDE
            transition.addTransition(Fade(Fade.IN))
            transition.addTransition(Fade(Fade.OUT))
            TransitionManager.beginDelayedTransition(binding.root, transition)

            binding.recyclerNews.alpha = ALFA_FILTER_HIDE
            binding.filterButton.setImageResource(R.drawable.check_icon)
            binding.countryTextInput.show()
            binding.cancelFilter.show()
        }
    }

    override fun errorCountry() {
        binding.countryTextInput.error = getString(R.string.errorSelectSources)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.countryTextInput.error = null
        }, DURATION_ERROR_INPUT)
    }


    override fun updateViewPagerEvent() {
        requireActivity().supportFragmentManager.fragments.forEach {
            if (it is EventTopNews) {
                (it as EventTopNews).updateViewPager()
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}