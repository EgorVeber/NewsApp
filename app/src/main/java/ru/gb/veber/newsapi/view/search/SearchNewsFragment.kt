package ru.gb.veber.newsapi.view.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.*
import com.google.android.material.datepicker.MaterialDatePicker
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchNewsFragmentBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.HistorySelectRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.SearchNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.topnews.pageritem.RecyclerListener
import ru.gb.veber.newsapi.view.topnews.pageritem.TopNewsAdapter


class SearchNewsFragment : MvpAppCompatFragment(), SearchNewsView, BackPressedListener {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AutoCompleteCountryAdapter
    private var dateInput: String = NOT_INPUT_DATE

    private var datePiker = MaterialDatePicker.Builder.datePicker()

    private var itemListener = object : RecyclerListenerHistorySelect {
        override fun clickHistoryItem(historySelect: HistorySelect) {
            presenter.openScreenNewsHistory(historySelect)
        }

        override fun deleteHistoryItem(historySelect: HistorySelect) {
            presenter.deleteHistory(historySelect)
        }
    }

    override fun emptyHistory() {
        Log.d("hideEmptyList", "hideEmptyList() called")
        binding.emptyHistory.show()
    }

    private val historySelectAdapter = HistorySelectAdapter(itemListener)


    override fun hideEmptyList() {
        Log.d("hideEmptyList", "hideEmptyList() called")
        binding.emptyHistory.hide()
    }

    private val presenter: SearchNewsPresenter by moxyPresenter {
        SearchNewsPresenter(
            App.instance.router,
            RoomRepoImpl(App.instance.newsDb.accountsDao()),
            HistorySelectRepoImpl(App.instance.newsDb.historySelectDao()),
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
        presenter.getSources()
        presenter.getHistorySelect()
        initialization()
    }

    override fun setHistory(list: List<HistorySelect>) {
        TransitionManager.beginDelayedTransition(binding.root)
        Log.d("TAG", "setHistory() called with: list = $list")
        historySelectAdapter.historySelectList = list
    }

    private fun initialization() {
        binding.searchView.setOnQueryTextListener(searchViewListener)

        with(binding.searchSpinnerCountry) {
            //setOnKeyListener(setOnKeyListenerSpinner)s
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
        }

        binding.recyclerHistory.adapter = historySelectAdapter
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())


        binding.checkBoxSearchSources.setOnCheckedChangeListener { _, b ->
            presenter.changeSearchCriteria(b)
        }

        binding.selectDate.setOnClickListener {
            createDatePiker()
        }
        binding.searchSourcesButton.setOnClickListener {
            var selectedItem = binding.spinnerSortBySources.selectedItem.toString()
            if (binding.spinnerSortBySources.selectedItemPosition == 0) {
                selectedItem = ""
            }
            presenter.openScreenAllNewsSources(dateInput,
                binding.searchSpinnerCountry.text.toString(),
                selectedItem)
        }

        binding.deleteHistoryAll.setOnClickListener {
            presenter.clearHistory()
        }
    }

    private fun createDatePiker() {
        datePiker.setTitleText("No later than 30 days")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build().also {
                it.show(requireActivity().supportFragmentManager, "s")
                it.addOnPositiveButtonClickListener {
                    presenter.pikerPositive(it)
                }
                it.addOnNegativeButtonClickListener {
                    presenter.pikerNegative()

                }
            }
    }

    override fun pikerPositive(l: Long) {
        dateInput = outputDateFormat.format(l)
        binding.selectDate.text = dateInput
    }

    override fun pikerNegative() {
        dateInput = NOT_INPUT_DATE
        binding.selectDate.text = "Select Date"
    }


    private val searchViewListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let { keyWord ->
                var searchIn: String = ""
                var sortBy: String = ""
                if (binding.spinnerSortBy.selectedItemPosition != 0) {
                    sortBy = binding.spinnerSortBy.selectedItem.toString()
                }

                if (binding.spinnerSearchIn.selectedItemPosition != 0) {
                    searchIn = binding.spinnerSearchIn.selectedItem.toString()
                }

                presenter.openScreenAllNews(keyWord,
                    searchIn,
                    sortBy,
                    binding.searchSpinnerCountry.text.toString())
            }
            binding.searchView.clearFocus();
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
    }

    override fun setSources(sources: List<Sources>) = with(binding) {
        adapter = AutoCompleteCountryAdapter(requireContext(), sources)
        searchSpinnerCountry.setAdapter(adapter)
    }

    override fun hideSelectHistory() {
        binding.groupHistory.hide()
    }

    override fun updateAdapter(likeSources: List<Sources>) {
        binding.searchTextInput.editText?.setText("")
        adapter = AutoCompleteCountryAdapter(requireContext(), likeSources)
        binding.searchSpinnerCountry.setAdapter(adapter)
        binding.searchSpinnerCountry.showDropDown()
    }

    override fun searchInShow() {

        TransitionSet().also { transition ->
            transition.duration = 400L
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
        }
        binding.groupSearchIn.show()
        binding.groupSources.hide()
    }

    override fun sourcesInShow() {

        TransitionSet().also { transition ->
            transition.duration = 400L
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
        }
        binding.groupSearchIn.hide()
        binding.groupSources.show()
    }

    override fun selectSources() {
        binding.searchTextInput.error = "Select Sources"
        Handler(Looper.getMainLooper()).postDelayed({
            binding.searchTextInput.error = null
        }, 2000L)
    }

    override fun errorDateInput() {
        binding.errorDateText.show()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.errorDateText.hide()
        }, 2000L)
    }

    private val setOnKeyListenerSpinner = View.OnKeyListener { p0, p1, p2 ->
        if (p1 == KeyEvent.KEYCODE_ENTER && p2?.action == KeyEvent.ACTION_DOWN) {
            binding.searchSpinnerCountry.hideKeyboard()
        }
        true
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