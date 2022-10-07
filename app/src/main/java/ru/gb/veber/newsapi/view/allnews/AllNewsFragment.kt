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

//    private val adapterSources = CustomAdapterSources()

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
            TODO("Not yet implemented")
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
    }


    private fun initialization() {

        presenter.getSources()
        binding.recyclerAllNews.adapter = newsAdapter
        binding.recyclerAllNews.layoutManager = LinearLayoutManager(requireContext())
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    presenter.loadNews(it)
                }
                binding.searchView.clearFocus();
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        binding.searchSpinnerCountry.setOnKeyListener { p0, p1, p2 ->
            if (p1 == KeyEvent.KEYCODE_ENTER && p2?.action == KeyEvent.ACTION_DOWN) {
                binding.searchSpinnerCountry.hideKeyboard()
            }
            true
        }

//        binding.searchSpinnerCountry.setAdapter(adapterSources)
        binding.searchSpinnerCountry.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.searchSpinnerCountry.showDropDown()
                }
            }
        binding.searchSpinnerCountry.threshold = 1
        binding.searchSpinnerCountry.onItemClickListener = listenerAdapter


        binding.actionFilterAllNews.setOnClickListener {
            presenter.loadNewsSources(binding.searchSpinnerCountry.text.toString())
        }
    }


    private val listenerAdapter =
        AdapterView.OnItemClickListener { parent, p1, position, id ->
            when (p1.id) {
                R.id.rootSelectSources -> {
                    Log.d("OnItemClickListener",
                        "null() called with: parent = $parent, p1 = $p1, position = $position, id = $id")
                }
                R.id.textSourcesName -> {
                    Log.d("OnItemClickListener",
                        "null() called with: parent = $parent, p1 = $p1, position = $position, id = $id")
                }
                R.id.checkSources -> {
                    Log.d("OnItemClickListener",
                        "null() called with: parent = $parent, p1 = $p1, position = $position, id = $id")
                }
            }

            val selectedItem = parent?.getItemAtPosition(position).toString()
            binding.searchSpinnerCountry.hideKeyboard()
        }

    override fun init() {

    }

    override fun setNews(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles
        binding.progressBarAllNews.hide()
        binding.recyclerAllNews.show()
    }

    override fun loading() {
        binding.progressBarAllNews.show()
        binding.recyclerAllNews.hide()
    }

    override fun setSources(sources: List<Sources>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),
            R.layout.select_sources_autocompile, R.id.textSourcesName, sources.map {
                it.name
            })
        binding.searchSpinnerCountry.setAdapter(adapter)
        binding.listView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(accountID: Int): AllNewsFragment {
            return AllNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}