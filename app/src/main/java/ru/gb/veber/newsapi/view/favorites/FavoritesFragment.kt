package ru.gb.veber.newsapi.view.favorites

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavotitesFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.PAGE
import ru.gb.veber.newsapi.utils.collapsed
import ru.gb.veber.newsapi.utils.expanded
import ru.gb.veber.newsapi.utils.formatDateDay
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.loadGlideNot
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.utils.stringFromData
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventShareLink
import ru.gb.veber.newsapi.view.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsAdapter
import ru.gb.veber.newsapi.view.topnews.fragment.recycler.TopNewsListener
import javax.inject.Inject

class FavoritesFragment : Fragment(), BackPressedListener,
    EventBehaviorToActivity {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val favoritesViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[FavoritesViewModel::class.java]
    }

    private var _binding: FavotitesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private var itemListener = object : TopNewsListener {

        override fun clickNews(article: Article) {
            favoritesViewModel.clickNews(article)
        }

        override fun deleteFavorites(article: Article) {
            favoritesViewModel.deleteFavorites(article)
        }

        override fun deleteHistory(article: Article) {
            favoritesViewModel.deleteHistory(article)
        }

        override fun clickGroupHistory(article: Article) {
            favoritesViewModel.clickGroupHistory(article)
        }

        override fun deleteGroupHistory(article: Article) {
            favoritesViewModel.deleteGroupHistory(article)
        }
    }

    private val historyAdapter = TopNewsAdapter(itemListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FavotitesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.instance.appComponent.inject(this)
        init()
        observeLiveData()
        favoritesViewModel.getAccountArticle(
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT,
            arguments?.getString(PAGE) ?: PAGE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return favoritesViewModel.onBackPressedRouter()
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
            favoritesViewModel.openScreenWebView(article.url)
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

    private fun observeLiveData() {
        favoritesViewModel.uiState.observe(viewLifecycleOwner, ::handleState)
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

    companion object {
        fun getInstance(page: String, accountID: Int) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putInt(ACCOUNT_ID, accountID)
                putString(PAGE, page)
            }
        }
    }
}