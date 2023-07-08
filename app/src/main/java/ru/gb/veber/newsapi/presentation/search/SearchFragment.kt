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
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.hideKeyboard
import ru.gb.veber.newsapi.common.extentions.observeFlow
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.extentions.showKeyboard
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.DURATION_ERROR_INPUT
import ru.gb.veber.newsapi.common.utils.NOT_INPUT_DATE
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchFragmentBinding
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.presentation.search.recycler.HistorySelectAdapter
import ru.gb.veber.newsapi.presentation.search.recycler.RecyclerListenerHistorySelect

class SearchFragment :
    NewsFragment<SearchFragmentBinding, SearchViewModel>(SearchFragmentBinding::inflate) {

    private lateinit var adapter: SourcesAdapterAutoCompile
    private var dateInput: String = NOT_INPUT_DATE
    private var datePiker = MaterialDatePicker.Builder.datePicker()
    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    private var itemListener = object : RecyclerListenerHistorySelect {
        override fun clickHistoryItem(historySelectModel: HistorySelectModel) {
            viewModel.onClickHistoryItem(historySelectModel)
        }

        override fun deleteHistoryItem(historySelectModel: HistorySelectModel) {
            viewModel.onClickHistoryIconDelete(historySelectModel)
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
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.searchSpinnerCountry.hideKeyboard()
            }
            setOnClickListener {
                showDropDown()
            }
            addTextChangedListener(searchSpinnerCountryTextWatcher)
        }
        recyclerHistory.adapter = historySelectAdapter
        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())

        checkBoxSearchSources.setOnCheckedChangeListener { _, cheked ->
            if (checkBoxSearchSources.isChecked) {
                if (binding.searchTextInput.editText?.text.isNullOrBlank()) searchButtonDeactive()
                else searchButtonActive()
            } else {
                if (binding.searchEdit.text.isNullOrBlank()) searchButtonDeactive()
                else searchButtonActive()
            }
            viewModel.changeSearchCriteria(cheked)
        }

        selectDate.setOnClickListener { createDatePiker() }

        searchSourcesButton.setOnClickListener {
            if (checkBoxSearchSources.isChecked) {

                val selectedItem = if (spinnerSortBySources.selectedItemPosition == 0) ""
                else spinnerSortBySources.selectedItem.toString()

                viewModel.onClickSearchSources(
                    date = dateInput,
                    sourcesName = searchSpinnerCountry.text.toString(),
                    sortBy = selectedItem
                )
            } else {
                var searchIn = ""
                var sortBy = PUBLISHER_AT

                if (binding.spinnerSortBy.selectedItemPosition != 0) {
                    sortBy = binding.spinnerSortBy.selectedItem.toString()
                }

                if (binding.spinnerSearchIn.selectedItemPosition != 0) {
                    searchIn = binding.spinnerSearchIn.selectedItem.toString()
                }
                viewModel.onClickSearch(
                    binding.searchEdit.text.toString(), searchIn, sortBy,
                    binding.searchSpinnerCountry.text.toString()
                )
            }
        }
        deleteHistoryAll.setOnClickListener {
            viewModel.onClickHistoryDelete()
        }
    }

    override fun onObserveData() {

        viewModel.dataStateFlow.observeFlow(this) { dataState ->
            when (dataState) {
                is SearchViewModel.DataState.SetHistorySelect -> setHistory(dataState.historySelectModel)
                is SearchViewModel.DataState.SetSources -> setSources(dataState.sources)
            }
        }

        viewModel.searchViewFlow.observeFlow(this) { state ->
            when (state) {
                is SearchViewModel.SearchState.SetDay -> pikerPositive(state.dateDay)
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
                SearchViewModel.SearchState.Started -> {}
            }
        }
        viewModel.historyStateFlow.observeFlow(this) { state ->
            when (state) {
                SearchViewModel.HistoryState.StatusTextHistoryShow -> {
                    statusTextHistoryShow()
                }
                SearchViewModel.HistoryState.StatusTextHistoryHide -> {
                    statusTextHistoryHide()
                }
                SearchViewModel.HistoryState.HideSelectHistory -> {
                    hideSelectHistory()
                }
                SearchViewModel.HistoryState.Started -> {}
            }
        }
    }

    override fun onViewInited() {
        viewModel.onViewInited(accountId)
    }

    private fun setHistory(list: List<HistorySelectModel>) {
        historySelectAdapter.historySelectModelList = list
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

    private fun pikerPositive(dateDay: String) {
        dateInput = dateDay
        binding.selectDate.text = dateInput
    }

    private fun pikerNegative() {
        dateInput = NOT_INPUT_DATE
        binding.selectDate.text = getString(R.string.selectDatePiker)
    }

    private fun setSources(sources: List<SourcesModel>) = with(binding) {
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
            searchEdit.visibility = View.INVISIBLE
        } else searchButtonActive()
        searchEnotBlock.hide()
    }

    private fun selectSources() {
        binding.searchTextInput.error = getString(R.string.errorSelectSources)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.searchTextInput.error = null
            }
        }, DURATION_ERROR_INPUT)
    }

    private fun enterKeys() {
        searchViewActive()
        binding.searchEdit.error = getString(R.string.error_search_key)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.searchEdit.error = null
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

    private fun statusTextHistoryShow() {
        binding.statusTextHistory.show()
    }

    private fun statusTextHistoryHide() {
        binding.statusTextHistory.hide()
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
