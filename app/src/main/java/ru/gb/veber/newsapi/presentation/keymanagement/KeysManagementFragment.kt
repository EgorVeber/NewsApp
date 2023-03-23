package ru.gb.veber.newsapi.presentation.keymanagement

import KeysDialogFragment
import android.transition.TransitionManager
import androidx.fragment.app.setFragmentResultListener
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.observeFlow
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.screen.WebViewScreen
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.common.utils.PAGE_GET_KEY
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.KeysManagementFragmentBinding
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.presentation.keymanagement.recycler.KeysListener
import ru.gb.veber.newsapi.presentation.keymanagement.recycler.KeysManagementAdapter
import javax.inject.Inject

class KeysManagementFragment :
    NewsFragment<KeysManagementFragmentBinding, KeysManagementViewModel>(
        KeysManagementFragmentBinding::inflate) {

    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    @Inject
    lateinit var router: Router

    private val listener = object : KeysListener {
        override fun checkBoxClick(apiKeyModel: ApiKeysModel) {
            viewModel.setNewKey(apiKeyModel)
        }

        override fun holderClick() {}
    }

    private val keysAdapter = KeysManagementAdapter(listener)

    override fun getViewModelClass(): Class<KeysManagementViewModel> =
        KeysManagementViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        binding.recyclerKeys.adapter = keysAdapter
        binding.recyclerKeys.itemAnimator = null

        binding.back.setOnClickListener {
            viewModel.back()
        }

        binding.addKeysButton.setOnClickListener {
            KeysDialogFragment().show(requireActivity().supportFragmentManager, "")
        }

        binding.goToSiteB.setOnClickListener {
            router.navigateTo(WebViewScreen(PAGE_GET_KEY))
        }
    }

    override fun onObserveData() {
        viewModel.keysManagementFlow.observeFlow(this) { state ->
            when (state) {
                is KeysManagementViewModel.KeysManagementState.SetKeys -> {
                    setKeys(state.keys)
                }
                KeysManagementViewModel.KeysManagementState.StartedState -> {}
                KeysManagementViewModel.KeysManagementState.EmptyList -> {
                    emptyList()
                }
            }
        }

        setFragmentResultListener(RESULT_KEY_LISTENER) { _, bundle ->
            val newKey = bundle.getString(RESULT_KEY_DATA) ?: ""
            viewModel.addKey(accountId, newKey)
        }
    }

    override fun onStartAction() {
        viewModel.getKeys(accountId)
    }

    private fun setKeys(keys: List<ApiKeysModel>) {
        TransitionManager.beginDelayedTransition(binding.root)
        keysAdapter.keys = keys
        binding.emptyList.hide()
    }

    private fun emptyList() {
        binding.emptyList.show()
    }

    companion object {
        const val RESULT_KEY_LISTENER = "RESULT_KEY_LISTENER"
        const val RESULT_KEY_DATA = "RESULT_KEY_DATA"
        fun getInstance(accountId: Int) = KeysManagementFragment().apply {
            this.accountId = accountId
        }
    }
}
