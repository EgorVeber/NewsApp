package ru.gb.veber.newsapi.view.favorites

import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.FavotitesFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.presenter.FavoritesPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter

class FavoritesFragment : MvpAppCompatFragment(), FavoritesView, BackPressedListener {

    private var _binding: FavotitesFragmentBinding? = null
    private val binding get() = _binding!!


    private val presenter: FavoritesPresenter by moxyPresenter {
        FavoritesPresenter(App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()))
    }
    private val historyAdapter = TopNewsAdapter {
        presenter.clickNews(it)
        //FavoritesViewPagerAdapter может пригодится
//        presenter.saveArticle(it,arguments?.getInt(ACCOUNT_ID)?: ACCOUNT_ID_DEFAULT)
    }

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
        presenter.getAccountLike(
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT,
            arguments?.getString(PAGE) ?: PAGE)
    }

    override fun init() {
        binding.likeRecycler.adapter = historyAdapter
        binding.likeRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun setSources(list: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        Log.d("TAG", "setSources() called with: list = $list")
        historyAdapter.articles = list
        binding.likeRecycler.show()
    }

    override fun loading() {
        binding.statusTextLike.hide()
    }

    override fun notAuthorized() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.statusTextLike.show()
        binding.statusTextLike.text = "Not authorized"
    }

    override fun emptyList() {
        binding.statusTextLike.show()
        binding.statusTextLike.text = "Empty List"
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
}