package ru.gb.veber.newsapi.view.sources

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.core.Completable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.SourcesFragmentBinding
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.repository.room.AccountSourcesRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.SourcesPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.subscribeDefault
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import java.util.concurrent.TimeUnit

class FragmentSources : MvpAppCompatFragment(), FragmentSourcesView, BackPressedListener {

    private var _binding: SourcesFragmentBinding? = null
    private val binding get() = _binding!!


    private val presenter: SourcesPresenter by moxyPresenter {
        SourcesPresenter(
            arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT,
            App.instance.router,
            AccountSourcesRepoImpl(App.instance.newsDb.accountSourcesDao()),
            SourcesRepoImpl(App.instance.newsDb.sourcesDao())
        )
    }


    private val listener = object : SourcesListener {
        override fun openUrl(url: String?) {
            url?.let {
                presenter.openWebView(url)
            }
        }

        override fun imageClick(source: Sources) {
            presenter.imageClick(source)
        }
    }

    private val sourcesAdapter = FragmentSourcesAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SourcesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getSources()
        initialization()
    }

    private fun initialization() {
        binding.recyclerSources.adapter = sourcesAdapter
        binding.recyclerSources.itemAnimator = null
        binding.recyclerSources.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("SetTextI18n")
    override fun setSources(list: List<Sources>) {
        TransitionManager.beginDelayedTransition(binding.root)
        sourcesAdapter.sources = list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {

        fun getInstance(accountID: Int): FragmentSources {
            return FragmentSources().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}