package ru.gb.veber.newsapi.view.allnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AllNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.presenter.AllNewsPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.hide
import ru.gb.veber.newsapi.utils.show
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.pageritem.RecyclerListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter


class AllNewsFragment : MvpAppCompatFragment(), AllNewsView, BackPressedListener {

    private var _binding: AllNewsFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: AllNewsPresenter by moxyPresenter {
        AllNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router, ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()))
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
        Log.d("arguments", arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT).toString())
        textSpinner()
        initialization()
    }


    private fun initialization() {
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

        binding.recyclerAllNews.adapter = newsAdapter
        binding.recyclerAllNews.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun textSpinner() {
//        var massive = resources.getStringArray(R.array.countryName)
//        var massive2 = resources.getStringArray(R.array.countryCode)
//        var listCountry = massive.zip(massive2).toMap()
//        var msa = resources.getStringArray(R.array.countryName)
//        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),
//            R.layout.select_country_list,
//            listCountry.keys.toTypedArray())
//        binding.searchSpinnerCountry.setAdapter(adapter)
//        binding.searchSpinnerCountry.threshold = 1
//        binding.searchSpinnerCountry.onItemClickListener = AdapterView.OnItemClickListener {
//                parent, _,
//                position, id,
//            ->
//            val selectedItem = parent.getItemAtPosition(position).toString()
//            var x = listCountry[selectedItem]
//            Log.d("TAG", x!!)
//            binding.searchSpinnerCountry.hideKeyboard()
//        }
//        binding.searchSpinnerCountry.onFocusChangeListener =
//            View.OnFocusChangeListener { _, hasFocus ->
//                if (hasFocus) {
//                    binding.searchSpinnerCountry.showDropDown()
//                }
//            }
    }

    override fun init() {

    }

    override fun setNews(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(binding.root)
        newsAdapter.articles = articles.toMutableList()
        binding.progressBarAllNews.hide()
        binding.recyclerAllNews.show()
    }

    override fun loading() {

        binding.progressBarAllNews.show()
        binding.recyclerAllNews.hide()
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