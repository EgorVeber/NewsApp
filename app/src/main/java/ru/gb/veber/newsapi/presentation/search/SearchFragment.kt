package ru.gb.veber.newsapi.presentation.search

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.datepicker.MaterialDatePicker
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.FORMAT_DATE_NEWS
import ru.gb.veber.newsapi.common.extentions.TIME_ZONE
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.hideKeyboard
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.DURATION_ERROR_INPUT
import ru.gb.veber.newsapi.common.utils.NOT_INPUT_DATE
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchFragmentBinding
import ru.gb.veber.newsapi.domain.models.HistorySelect
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.presentation.search.recycler.HistorySelectAdapter
import ru.gb.veber.newsapi.presentation.search.recycler.RecyclerListenerHistorySelect
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment :
    NewsFragment<SearchFragmentBinding, SearchViewModel>(SearchFragmentBinding::inflate) {

    private lateinit var adapter: SourcesAdapterAutoCompile
    private var dateInput: String = NOT_INPUT_DATE
    private var datePiker = MaterialDatePicker.Builder.datePicker()
    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    private var itemListener = object : RecyclerListenerHistorySelect {
        override fun clickHistoryItem(historySelect: HistorySelect) {
            viewModel.openScreenNewsHistory(historySelect)
        }

        override fun deleteHistoryItem(historySelect: HistorySelect) {
            viewModel.deleteHistory(historySelect)
        }
    }

    private val searchViewListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let { keyWord ->
                var searchIn = ""
                var sortBy = PUBLISHER_AT

                if (binding.spinnerSortBy.selectedItemPosition != 0) {
                    sortBy = binding.spinnerSortBy.selectedItem.toString()
                }

                if (binding.spinnerSearchIn.selectedItemPosition != 0) {
                    searchIn = binding.spinnerSearchIn.selectedItem.toString()
                }
                viewModel.openScreenAllNews(
                    keyWord, searchIn, sortBy,
                    binding.searchSpinnerCountry.text.toString()
                )
            }
            binding.searchView.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
    }

    private val historySelectAdapter = HistorySelectAdapter(itemListener)

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() = with(binding) {
        searchView.setOnQueryTextListener(searchViewListener)
        with(searchSpinnerCountry) {
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
            addTextChangedListener(searchSpinnerCountryTextWatcher)
        }
        recyclerHistory.adapter = historySelectAdapter
        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())

        checkBoxSearchSources.setOnCheckedChangeListener { _, b ->
            viewModel.changeSearchCriteria(b)
        }
        selectDate.setOnClickListener {
            createDatePiker()
        }
        searchSourcesButton.setOnClickListener {
            var selectedItem = spinnerSortBySources.selectedItem.toString()
            if (spinnerSortBySources.selectedItemPosition == 0) {
                selectedItem = ""
            }
            viewModel.openScreenAllNewsSources(
                dateInput,
                searchSpinnerCountry.text.toString(),
                selectedItem
            )
        }
        deleteHistoryAll.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onObserveData() {
        viewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchViewModel.SearchState.PikerPositive -> {
                    pikerPositive(state.l)
                }
                is SearchViewModel.SearchState.SetHistory -> {
                    setHistory(state.list)
                }
                is SearchViewModel.SearchState.SetSources -> {
                    setSources(state.list)
                }
                SearchViewModel.SearchState.SearchInShow -> {
                    searchInShow()
                }
                SearchViewModel.SearchState.ErrorDateInput -> {
                    errorDateInput()
                }
                SearchViewModel.SearchState.PikerNegative -> {
                    pikerNegative()
                }
                SearchViewModel.SearchState.SelectSources -> {
                    selectSources()
                }
                SearchViewModel.SearchState.SourcesInShow -> {
                    sourcesInShow()
                }
            }
        }
        viewModel.subscribeVisibility().observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchViewModel.VisibilityState.EmptyHistory -> {
                    emptyHistory()
                }
                SearchViewModel.VisibilityState.HideEmptyList -> {
                    hideEmptyList()
                }
                SearchViewModel.VisibilityState.HideSelectHistory -> {
                    hideSelectHistory()
                }
            }
        }
    }

    override fun onStartAction() {
        viewModel.getSources()
        viewModel.getHistorySelect()
    }

    private fun setHistory(list: List<HistorySelect>) {
        historySelectAdapter.historySelectList = list
    }

    private val searchSpinnerCountryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int,
        ) {
            binding.searchSourcesButton.alpha = 0.5F
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun createDatePiker() {
        datePiker.setTitleText(getString(R.string.inputDateThirty))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build().also {
                it.show(requireActivity().supportFragmentManager, TAG_DATE_PIKER)
                it.addOnPositiveButtonClickListener { timeMillis ->
                    viewModel.pikerPositive(timeMillis)
                }
                it.addOnNegativeButtonClickListener {
                    viewModel.pikerNegative()

                }
            }
    }


    private fun pikerPositive(l: Long) {
        val outputDateFormat = SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(TIME_ZONE)
        }
        dateInput = outputDateFormat.format(l)
        binding.selectDate.text = dateInput
    }

    private fun pikerNegative() {
        dateInput = NOT_INPUT_DATE
        binding.selectDate.text = getString(R.string.selectDatePiker)
    }

    private fun setSources(sources: List<Sources>) = with(binding) {
        adapter = SourcesAdapterAutoCompile(requireContext(), sources)
        searchSpinnerCountry.setAdapter(adapter)
    }

    private fun hideSelectHistory() {
        binding.groupHistory.hide()
    }

    private fun searchInShow() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_CHANGE_FILTER
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.groupSearchIn.show()
            binding.groupSources.hide()
        }
    }

    private fun sourcesInShow() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_CHANGE_FILTER
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.groupSearchIn.hide()
            binding.groupSources.show()
        }
    }

    private fun selectSources() {
        binding.searchTextInput.error = getString(R.string.errorSelectSources)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.searchTextInput.error = null
            }
        }, DURATION_ERROR_INPUT)
    }

    private fun errorDateInput() {
        binding.errorDateText.show()
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.errorDateText.hide()
            }
        }, DURATION_ERROR_INPUT)
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { _, _, _, _ ->
        binding.searchSpinnerCountry.hideKeyboard()
        binding.searchSourcesButton.alpha = 1F
    }

    private fun emptyHistory() {
        binding.emptyHistory.show()
    }

    private fun hideEmptyList() {
        binding.emptyHistory.hide()
    }

    companion object {
        const val TAG_DATE_PIKER = "TAG_DATE_PIKER"
        const val PUBLISHER_AT = "publishedAt"
        const val DURATION_CHANGE_FILTER = 400L
        fun getInstance(accountID: Int): SearchFragment {
            return SearchFragment().apply {
                this.accountId = accountID
            }
        }
    }
}
