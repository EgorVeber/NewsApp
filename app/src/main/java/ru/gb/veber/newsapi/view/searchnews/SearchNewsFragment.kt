package ru.gb.veber.newsapi.view.searchnews

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.transition.TransitionManager
import com.google.android.material.datepicker.MaterialDatePicker
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SearchNewsFragmentBinding
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.RoomRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.SearchNewsPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener


class SearchNewsFragment : MvpAppCompatFragment(), SearchNewsView, BackPressedListener {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AutoCompleteCountryAdapter
    private var dateInput: String = NOT_INPUT_DATE

    private var datePiker = MaterialDatePicker.Builder.datePicker()


    private val presenter: SearchNewsPresenter by moxyPresenter {
        SearchNewsPresenter(
            App.instance.router,
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
        presenter.getSources()
        initialization()
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

        binding.checkBoxSearchSources.setOnCheckedChangeListener { _, b ->
            // presenter.notifyAdapter(b)
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
    }

    private fun createDatePiker() {
        datePiker.setTitleText("No later than 30 days")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build().also {
                it.show(requireActivity().supportFragmentManager, "s")
                it.addOnPositiveButtonClickListener {
                    dateInput = outputDateFormat.format(it)
                    binding.selectDate.text = dateInput
                }
                it.addOnNegativeButtonClickListener {
                    dateInput = NOT_INPUT_DATE
                    binding.selectDate.text = "Select Date"
                }
            }
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
        // binding.grouphistopry.hide()
    }

    override fun updateAdapter(likeSources: List<Sources>) {
        binding.searchTextInput.editText?.setText("")
        adapter = AutoCompleteCountryAdapter(requireContext(), likeSources)
        binding.searchSpinnerCountry.setAdapter(adapter)
        binding.searchSpinnerCountry.showDropDown()
    }

    override fun searchInShow() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.groupSearchIn.show()
        binding.groupSources.hide()
    }

    override fun sourcesInShow() {
        TransitionManager.beginDelayedTransition(binding.root)
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