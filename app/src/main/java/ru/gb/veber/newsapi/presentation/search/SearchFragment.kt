package ru.gb.veber.newsapi.presentation.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.datepicker.MaterialDatePicker
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.*
import ru.gb.veber.newsapi.common.utils.*
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

    private val historySelectAdapter = HistorySelectAdapter(itemListener)

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!binding.searchEdit.text.isNullOrBlank()) {
            binding.searchEdit.show()
            binding.searchEdit.requestFocus()
            binding.searchEdit.setText(binding.searchEdit.text.toString())
            searchViewDeactive()
        }
        binding.searchTextInput.error = null
        binding.searchEdit.error = null
    }

    override fun onInitView() = with(binding) {
        searchIcon.setOnClickListener { searchViewActive() }
        searchView.setOnClickListener { searchViewActive() }
        searchView.setEndIconOnClickListener {
            searchEdit.text?.clear()
            if (searchEnotBlock.visibility == View.GONE) searchViewDeactive()
        }
        searchEnotBlock.setOnClickListener { searchViewDeactive() }
        searchEdit.setOnFocusChangeListener { _, b ->
            if (b) searchEnotBlock.show()
        }
        searchEdit.setOnEditorActionListener { _, _, _ ->
            searchViewDeactive()
            false
        }
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
            if (checkBoxSearchSources.isChecked) {
                if (binding.searchTextInput.editText?.text.isNullOrBlank()) searchButtonDeactive()
                else searchButtonActive()
            }
            else {
                if (binding.searchEdit.text.isNullOrBlank()) searchButtonDeactive()
                else searchButtonActive()
            }
            viewModel.changeSearchCriteria(b)
        }
        selectDate.setOnClickListener {
            createDatePiker()
        }
        searchSourcesButton.setOnClickListener {
            if (checkBoxSearchSources.isChecked) {

                val selectedItem = if (spinnerSortBySources.selectedItemPosition == 0) ""
                else spinnerSortBySources.selectedItem.toString()

                viewModel.openScreenAllNewsSources(
                    date = dateInput,
                    sourcesName = searchSpinnerCountry.text.toString(),
                    sortBy = selectedItem
                )
            }
            else {
                var searchIn = ""
                var sortBy = PUBLISHER_AT

                if (binding.spinnerSortBy.selectedItemPosition != 0) {
                    sortBy = binding.spinnerSortBy.selectedItem.toString()
                }

                if (binding.spinnerSearchIn.selectedItemPosition != 0) {
                    searchIn = binding.spinnerSearchIn.selectedItem.toString()
                }
                viewModel.openScreenAllNews(
                    binding.searchEdit.text.toString(), searchIn, sortBy,
                    binding.searchSpinnerCountry.text.toString()
                )
            }
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
                SearchViewModel.SearchState.EnterKeys -> {
                    enterKeys()
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
            searchButtonDeactive()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank()) searchButtonActive()
        }
    }

    private fun searchButtonActive() {
        binding.searchSourcesButton.alpha = 1F
    }

    private fun searchButtonDeactive() {
        binding.searchSourcesButton.alpha = 0.5F
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

    private fun searchViewActive() = with(binding) {
        searchEnotBlock.show()
        searchEdit.show()
        searchEdit.requestFocus()
        showKeyboard()
    }

    private fun searchViewDeactive() = with(binding) {
        searchEdit.clearFocus()
        searchEdit.hideKeyboard()
        if (searchEdit.text?.isEmpty() == true) {
            searchButtonDeactive()
            searchEdit.visibility = View.INVISIBLE }
        else searchButtonActive()
        searchEnotBlock.hide()
    }

    private fun selectSources() {
        binding.searchTextInput.error = getString(R.string.errorSelectSources)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { binding.searchTextInput.error = null }
        }, DURATION_ERROR_INPUT)
    }

    private fun enterKeys() {
        searchViewActive()
        binding.searchEdit.error = getString(R.string.error_search_key)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { binding.searchEdit.error = null }
        }, DURATION_ERROR_INPUT)
    }

    private fun errorDateInput() {
        binding.errorDateText.show()
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { binding.errorDateText.hide() }
        }, DURATION_ERROR_INPUT)
    }

    private val listenerAdapter = AdapterView.OnItemClickListener { _, _, _, _ ->
        binding.searchSpinnerCountry.hideKeyboard()
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
