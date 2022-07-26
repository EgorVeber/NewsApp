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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavotitesFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.presenter.FavoritesPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.pageritem.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.topnews.pageritem.RecyclerListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter

class FavoritesFragment : MvpAppCompatFragment(), FavoritesView, BackPressedListener,
    EventBehaviorToActivity {

    private var _binding: FavotitesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private val presenter: FavoritesPresenter by moxyPresenter {
        FavoritesPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun clickNews(article: Article) {
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
            presenter.openScreenWebView(article.url)
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

    private var itemListener = object : RecyclerListener {

        override fun clickNews(article: Article) {
            presenter.clickNews(article)
        }

        override fun deleteFavorites(article: Article) {
            presenter.deleteFavorites(article)
        }

        override fun deleteHistory(article: Article) {
            presenter.deleteHistory(article)
        }

        override fun clickGroupHistory(article: Article) {
            presenter.clickGroupHistory(article)
        }

        override fun deleteGroupHistory(article: Article) {
            presenter.deleteGroupHistory(article)
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
        presenter.getAccountArticle(
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT,
            arguments?.getString(PAGE) ?: PAGE)
    }

    override fun init() {
        binding.likeRecycler.adapter = historyAdapter
        binding.likeRecycler.itemAnimator = null
        binding.likeRecycler.layoutManager = LinearLayoutManager(requireContext())
        bSheetB = BottomSheetBehavior.from(binding.behaviorInclude.bottomSheetContainer)
        binding.behaviorInclude.imageFavorites.hide()
    }

    override fun setSources(list: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        if (list.isEmpty()) {
            emptyList()
        }
        historyAdapter.articles = list
        binding.likeRecycler.show()
    }

    override fun loading() {
        binding.statusTextLike.hide()
    }

    override fun notAuthorized() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(R.string.notAuthorized)
    }

    override fun emptyList() {
        binding.statusTextLike.show()
        binding.statusTextLike.text = getString(R.string.empty_list)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(page: String, accountID: Int) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putInt(ACCOUNT_ID, accountID)
                putString(PAGE, page)
            }
        }
    }

    override fun getStateBehavior(): Int {
        return bSheetB.state
    }

    override fun setStateBehavior() {
        bSheetB.collapsed()
    }
}