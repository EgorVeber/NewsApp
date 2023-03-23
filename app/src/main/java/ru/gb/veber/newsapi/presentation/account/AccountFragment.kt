package ru.gb.veber.newsapi.presentation.account

import android.annotation.SuppressLint
import android.transition.TransitionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.base.NewsFragment
import ru.gb.veber.newsapi.common.extentions.hide
import ru.gb.veber.newsapi.common.extentions.show
import ru.gb.veber.newsapi.common.extentions.showSnackBarError
import ru.gb.veber.newsapi.common.extentions.showText
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AccountFragmentBinding
import ru.gb.veber.newsapi.databinding.ConfirmDialogBinding
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.presentation.activity.EventLogoutAccountScreen

class AccountFragment : NewsFragment<AccountFragmentBinding, AccountViewModel>
    (AccountFragmentBinding::inflate) {

    private var accountId: Int by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    override fun getViewModelClass(): Class<AccountViewModel> = AccountViewModel::class.java

    override fun onInject() {
        App.instance.appComponent.inject(this)
    }

    override fun onInitView() {
        binding.deleteAccount.setOnClickListener {
            viewModel.setStateShowDialog(
                title = getString(R.string.deleteAccount),
                message = getString(R.string.warning_account),
                positive = getString(R.string.delete)
            ) {
                viewModel.deleteAccount()
            }
        }
        binding.totalHistory.setOnClickListener {
            viewModel.setStateShowDialog(
                title = getString(R.string.delete_history),
                message = getString(R.string.warning_history),
                positive = getString(R.string.delete)
            ) {
                viewModel.clearHistory()
            }
        }
        binding.totalFavorites.setOnClickListener {
            viewModel.setStateShowDialog(
                title = getString(R.string.delete_favorites),
                message = getString(R.string.warning_favorites),
                positive = getString(R.string.delete)
            ) {
                viewModel.clearFavorites()
            }
        }
        binding.totalSources.setOnClickListener {
            viewModel.setStateShowDialog(
                title = getString(R.string.delete_sources),
                message = getString(R.string.warning_sources),
                positive = getString(R.string.delete)
            ) {
                viewModel.clearSources()
            }
        }
        binding.logout.setOnClickListener {
            viewModel.setStateShowDialog(
                title = getString(R.string.confirm_logout),
                message = getString(R.string.warning_logout),
                positive = getString(R.string.logout)
            ) {
                viewModel.logout()
                viewModel.setStateSetBottomNavigationIcon()
            }
        }
        binding.editInformation.setOnClickListener { viewModel.openScreenEditAccount() }
        binding.saveHistorySwitch.setOnCheckedChangeListener { _, b ->
            viewModel.updateAccountSaveHistory(b)
        }
        binding.saveHistorySelectSwitch.setOnCheckedChangeListener { _, b ->
            viewModel.updateAccountSaveHistorySelect(b)
        }
        binding.showFavorites.setOnCheckedChangeListener { _, b ->
            viewModel.updateAccountShowListFavorite(b)
        }
        binding.switchDarkTheme.setOnClickListener { viewModel.setTheme(binding.switchDarkTheme.isChecked) }
        binding.fontSize.setOnClickListener { it.showText(getString(R.string.notAvailable)) }
        binding.imageUpload.setOnClickListener { it.showText(getString(R.string.notAvailable)) }
        binding.customizeCategory.setOnClickListener { viewModel.openScreenCustomizeCategory() }
        binding.notificationFirebase.setOnClickListener { it.showText(getString(R.string.notAvailable)) }
        binding.aboutInformation.setOnClickListener { it.showText(getString(R.string.notAvailable)) }
        binding.privacyPolicy.setOnClickListener { viewModel.openScreenWebView(getString(R.string.team_site)) }
        binding.supportLinear.setOnClickListener { it.showText(getString(R.string.notAvailable)) }
        binding.keysManagementLl.setOnClickListener {
            viewModel.openScrenKeysManagement(accountId)
        }
    }

    override fun onObserveData() {
        viewModel.subscribe(accountId).observe(viewLifecycleOwner) { state ->
            when (state) {
                is AccountViewModel.AccountViewState.SetAccountInfo -> {
                    setAccountInfo(account = state.account, themePrefs = state.theme)
                }
                is AccountViewModel.AccountViewState.AccountDialog -> {
                    showDialog(
                        title = state.title,
                        message = state.message,
                        positive = state.positive,
                        onClick = state.onClick
                    )
                }
                AccountViewModel.AccountViewState.Loading -> {
                    loading()
                }
                AccountViewModel.AccountViewState.SetBottomNavigationIcon -> {
                    setBottomNavigationIcon()
                }
                AccountViewModel.AccountViewState.ClearHistory -> {
                    clearHistory()
                }
                AccountViewModel.AccountViewState.ClearFavorites -> {
                    clearFavorites()
                }
                AccountViewModel.AccountViewState.ClearSources -> {
                    clearSources()
                }
                AccountViewModel.AccountViewState.ToastDelete -> {
                    toastDelete()
                }
                AccountViewModel.AccountViewState.RecreateTheme -> {
                    recreateTheme()
                }
            }
        }
    }

    private fun toastDelete() {
        binding.root.showText(getString(R.string.textDelete))
    }

    private fun recreateTheme() {
        activity?.recreate()
    }

    @SuppressLint("SetTextI18n")
    fun setAccountInfo(account: Account, themePrefs: Int) {
        binding.userName.text = account.userName
        binding.userEmail.text = account.email
        binding.totalFavoritesText.text = getString(R.string.total_favorites) + account.totalFavorites
        binding.totalHistoryText.text = getString(R.string.total_history) + account.totalHistory
        binding.totalSourcesText.text = getString(R.string.total_sources) + account.totalSources
        binding.saveHistorySwitch.isChecked = account.saveHistory
        binding.saveHistorySelectSwitch.isChecked = account.saveSelectHistory
        binding.showFavorites.isChecked = account.displayOnlySources
        binding.switchDarkTheme.isChecked = themePrefs != R.style.Theme_NewsAPI
        TransitionManager.beginDelayedTransition(binding.root)
        binding.nestedScrollAccount.show()
    }

    private fun loading() {
        binding.nestedScrollAccount.hide()
    }

    private fun setBottomNavigationIcon() {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetDefaultIcon()
    }

    private fun showDialog(
        title: String,
        message: String,
        positive: String,
        onClick: () -> Unit
    ) {
        val dialogBinding = ConfirmDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setTitle(title)
            .setMessage(message)
            .setView(dialogBinding.root)
            .create()
        dialog.show()

        dialogBinding.negativeButton.setOnClickListener { dialog.dismiss() }
        dialogBinding.positiveButton.text = positive
        dialogBinding.positiveButton.setOnClickListener {
            onClick()
            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun clearHistory() {
        binding.totalHistoryText.text = getString(R.string.total_history) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearHistory), "", {})
    }

    @SuppressLint("SetTextI18n")
    fun clearFavorites() {
        binding.totalFavoritesText.text = getString(R.string.total_favorites) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearFavorites), "", {})
    }

    @SuppressLint("SetTextI18n")
    fun clearSources() {
        binding.totalSourcesText.text = getString(R.string.total_sources) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearSources), "", {})
    }

    companion object {
        fun getInstance(accountID: Int): AccountFragment {
            return AccountFragment().apply {
                this.accountId = accountID
            }
        }
    }
}
