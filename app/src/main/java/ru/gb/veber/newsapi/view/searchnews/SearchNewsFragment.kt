package ru.gb.veber.newsapi.view.searchnews

import android.R
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.transition.TransitionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchNewsFragmentBinding
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.SearchNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class SearchNewsFragment : MvpAppCompatFragment(), SearchNewsView, BackPressedListener {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: SearchNewsPresenter by moxyPresenter {
        SearchNewsPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            ArticleRepoImpl(App.instance.newsDb.articleDao()),
            RoomRepoImpl(App.instance.newsDb.accountsDao()),
            arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT) ?: ACCOUNT_ID_DEFAULT,
            SourcesRepoImpl(App.instance.newsDb.sourcesDao()),
            AccountSourcesRepoImpl(App.instance.newsDb.accountSourcesDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SearchNewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
    }

    override fun init() {

    }

    override fun setSources(sources: List<Sources>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),
            R.layout.simple_list_item_1, sources.map {
                it.name
            })
        binding.searchSpinnerCountry.setAdapter(adapter)
    }


    private fun initialization() {
        presenter.getSources()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    presenter.openSearchNews(it)
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

//      binding.searchSpinnerCountry.setAdapter(adapterSources)
        binding.searchSpinnerCountry.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.searchSpinnerCountry.showDropDown()
                }
            }
        binding.searchSpinnerCountry.threshold = 1
        binding.searchSpinnerCountry.onItemClickListener = listenerAdapter

        binding.checkBoxSearchSources.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root)
            if (!binding.checkBoxSearchSources.isChecked) {
                binding.searchView.show()
                binding.searchSourcesButton.hide()
            } else {
                binding.searchView.hide()
                binding.searchSourcesButton.show()
            }
        }
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { parent, p1, position, id ->
        binding.searchSpinnerCountry.hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {
        fun getInstance(accountID: Int): SearchNewsFragment {
            return SearchNewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}