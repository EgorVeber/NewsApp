package ru.gb.veber.newsapi.view.searchnews

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

class SearchNewsFragment : MvpAppCompatFragment(), SearchNewsView, BackPressedListener {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AutoCompleteCountryAdapter

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
            threshold = 1
            onItemClickListener = listenerAdapter
            setOnClickListener {
                showDropDown()
            }
            //setOnKeyListener(setOnKeyListenerSpinner)
        }

        binding.checkBoxSearchSources.setOnCheckedChangeListener { _, b ->
           // presenter.notifyAdapter(b)
            TransitionManager.beginDelayedTransition(binding.root)
            if (!binding.checkBoxSearchSources.isChecked) {
                binding.searchView.show()
                binding.searchSourcesButton.hide()
            } else {
                binding.searchView.hide()
                binding.searchSourcesButton.show()
            }
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date news")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "s")
        datePicker.addOnPositiveButtonClickListener {
            val text = outputDateFormat.format(it)
            Log.d("TAG", text)
        }
    }

    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val searchViewListener = object : SearchView.OnQueryTextListener {
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
    }

    override fun setSources(sources: List<Sources>) = with(binding) {
        adapter = AutoCompleteCountryAdapter(requireContext(), sources)
        searchSpinnerCountry.setAdapter(adapter)
    }

    override fun hideSelectHistory() {
        binding.grouphistopry.hide()
    }

    override fun updateAdapter(likeSources: List<Sources>) {
        binding.searchTextInput.editText?.setText("")
        adapter = AutoCompleteCountryAdapter(requireContext(), likeSources)
        binding.searchSpinnerCountry.setAdapter(adapter)
        binding.searchSpinnerCountry.showDropDown()
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