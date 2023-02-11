package ru.gb.veber.newsapi.view.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.datepicker.MaterialDatePicker
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchFragmentBinding
import ru.gb.veber.newsapi.model.HistorySelect
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.presenter.SearchPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import java.text.SimpleDateFormat
import java.util.*


class SearchFragment : MvpAppCompatFragment(),
    ru.gb.veber.newsapi.view.search.SearchView, BackPressedListener {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SourcesAdapterAutoCompile
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

    private val historySelectAdapter = HistorySelectAdapter(itemListener)


    private val presenter: SearchPresenter by moxyPresenter {
        SearchPresenter(arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
            ?: ACCOUNT_ID_DEFAULT).apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getSources()
        presenter.getHistorySelect()
        initialization()
    }

    override fun setHistory(list: List<HistorySelect>) {
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

    private fun initialization() {
        binding.searchView.setOnQueryTextListener(searchViewListener)

        with(binding.searchSpinnerCountry) {
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
            addTextChangedListener(searchSpinnerCountryTextWatcher)
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
        datePiker.setTitleText(getString(R.string.inputDateThirty))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build().also {
                it.show(requireActivity().supportFragmentManager, TAG_DATE_PIKER)
                it.addOnPositiveButtonClickListener {
                    presenter.pikerPositive(it)
                }
                it.addOnNegativeButtonClickListener {
                    presenter.pikerNegative()

                }
            }
    }


    override fun pikerPositive(l: Long) {
        var outputDateFormat = SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(TIME_ZONE)
        }
        dateInput = outputDateFormat.format(l)
        binding.selectDate.text = dateInput
    }

    override fun pikerNegative() {
        dateInput = NOT_INPUT_DATE
        binding.selectDate.text = getString(R.string.selectDatePiker)
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
                presenter.openScreenAllNews(keyWord, searchIn, sortBy,
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
        adapter = SourcesAdapterAutoCompile(requireContext(), sources)
        searchSpinnerCountry.setAdapter(adapter)
    }

    override fun hideSelectHistory() {
        binding.groupHistory.hide()
    }

    override fun updateAdapter(likeSources: List<Sources>) {
        binding.searchTextInput.editText?.setText("")
        adapter = SourcesAdapterAutoCompile(requireContext(), likeSources)
        binding.searchSpinnerCountry.setAdapter(adapter)
        binding.searchSpinnerCountry.showDropDown()
    }

    override fun searchInShow() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_CHANGE_FILTER
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.groupSearchIn.show()
            binding.groupSources.hide()
        }
    }

    override fun sourcesInShow() {
        TransitionSet().also { transition ->
            transition.duration = DURATION_CHANGE_FILTER
            transition.addTransition(Fade())
            transition.addTransition(ChangeBounds())
            TransitionManager.beginDelayedTransition(binding.root, transition)
            binding.groupSearchIn.hide()
            binding.groupSources.show()
        }
    }

    override fun selectSources() {
        binding.searchTextInput.error = getString(R.string.errorSelectSources)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                binding.searchTextInput.error = null
            }
        }, DURATION_ERROR_INPUT)
    }

    override fun errorDateInput() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    override fun emptyHistory() {
        binding.emptyHistory.show()
    }

    override fun hideEmptyList() {
        binding.emptyHistory.hide()
    }

    companion object {
        const val TAG_DATE_PIKER = "TAG_DATE_PIKER"
        const val PUBLISHER_AT = "publishedAt"
        const val DURATION_CHANGE_FILTER = 400L
        fun getInstance(accountID: Int): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}