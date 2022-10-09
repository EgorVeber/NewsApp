package ru.gb.veber.newsapi.view.allnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AllNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.AllNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.pageritem.RecyclerListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter


class AllNewsFragment : MvpAppCompatFragment(), AllNewsView, BackPressedListener {

    private var _binding: AllNewsFragmentBinding? = null
    private val binding get() = _binding!!


    private val presenter: AllNewsPresenter by moxyPresenter {
        AllNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()),
            arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT) ?: ACCOUNT_ID_DEFAULT,
            SourcesRepoImpl(App.instance.newsDb.sourcesDao()),
            AccountSourcesRepoImpl(App.instance.newsDb.accountSourcesDao()))
    }

    private var itemListener = object : RecyclerListener {

        override fun clickNews(article: Article) {
//            presenter.clickNews(article)
//            presenter.saveArticle(article, arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        }

        override fun deleteFavorites(article: Article) {

        }
    }

    private val newsAdapter = TopNewsAdapter(itemListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AllNewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //textSpinner()
        initialization()

        arguments?.let { arguments ->

            binding.textArgument.text = "ACCOUNT_ID = " + arguments.getInt(ACCOUNT_ID)
                .toString() + "\nKEY_WORD = " + arguments.getString(KEY_WORD).toString() +
                    "\nSEARCH_IN = " + arguments.getString(SEARCH_IN)
                .toString() + "\nSORT_BY_KEY_WORD = " + arguments.getString(SORT_BY_KEY_WORD)
                .toString() +
                    "\nSORT_BY_SOURCES = " + arguments.getString(SORT_BY_SOURCES).toString() +
                    "\nSOURCES_NAME = " + arguments.getString(SOURCES_NAME)
                .toString() + "\nDATE_SOURCES = " + arguments.getString(DATE_SOURCES).toString()

            Log.d("onViewCreatedAllNews", "ACCOUNT_ID = " + arguments.getInt(ACCOUNT_ID).toString())
            Log.d("onViewCreatedAllNews", "KEY_WORD = " + arguments.getString(KEY_WORD).toString())
            Log.d("onViewCreatedAllNews",
                "SEARCH_IN = " + arguments.getString(SEARCH_IN).toString())
            Log.d("onViewCreatedAllNews",
                "SORT_BY_KEY_WORD = " + arguments.getString(SORT_BY_KEY_WORD).toString())
            Log.d("onViewCreatedAllNews",
                "SORT_BY_SOURCES = " + arguments.getString(SORT_BY_SOURCES).toString())
            Log.d("onViewCreatedAllNews",
                "SOURCES_NAME = " + arguments.getString(SOURCES_NAME).toString())
            Log.d("onViewCreatedAllNews",
                "DATE_SOURCES = " + arguments.getString(DATE_SOURCES).toString())
        }
    }


    private fun initialization() {

//        binding.recyclerAllNews.adapter = newsAdapter
//        binding.recyclerAllNews.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun setNews(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        binding.progressBarAllNews.hide()
//        binding.recyclerAllNews.show()
    }

    override fun loading() {
        binding.progressBarAllNews.show()
//        binding.recyclerAllNews.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(
            accountID: Int,
            keyWord: String?,
            searchIn: String?,
            sortByKeyWord: String?,
            sortBySources: String?,
            sourcesName: String?,
            dateSources: String?,
        ): AllNewsFragment {
            return AllNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                    putString(KEY_WORD, keyWord)
                    putString(SEARCH_IN, searchIn)
                    putString(SORT_BY_KEY_WORD, sortByKeyWord)
                    putString(SORT_BY_SOURCES, sortBySources)
                    putString(SOURCES_NAME, sourcesName)
                    putString(DATE_SOURCES, dateSources)
                }
            }
        }
    }
}