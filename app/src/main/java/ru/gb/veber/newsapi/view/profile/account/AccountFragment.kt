package ru.gb.veber.newsapi.view.profile.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import moxy.MvpAppCompatFragment
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AccountFragmentBinding
import ru.gb.veber.newsapi.databinding.ConfirmDialogBinding
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen
import javax.inject.Inject

class AccountFragment : MvpAppCompatFragment(), BackPressedListener {

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val accountViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AccountViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT).apply {
            accountViewModel.getAccountSettings(this)
        }
        observeLiveData()
    }

    private fun observeLiveData() {
        accountViewModel.uiState.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: AccountViewState) {
        when (state) {
            is AccountViewState.SetAccountInfo -> {
                setAccountInfo(account = state.account, themePrefs = state.theme)
            }
            is AccountViewState.ShowDialog -> {
                showDialog(
                    title = state.title,
                    message = state.message,
                    positive = state.positive,
                    onClick = state.onClick
                )
            }
            is AccountViewState.Init -> {
                init()
            }
            is AccountViewState.Loading -> {
                loading()
            }
            is AccountViewState.SetBottomNavigationIcon -> {
                setBottomNavigationIcon()
            }
            is AccountViewState.ClearHistory -> {
                clearHistory()
            }
            is AccountViewState.ClearFavorites -> {
                clearFavorites()
            }
            is AccountViewState.ClearSources -> {
                clearSources()
            }
            is AccountViewState.ToastDelete -> {
                toastDelete()
            }
            is AccountViewState.RecreateTheme -> {
                recreateTheme()
            }
        }
    }

    fun init() {
        val accountID = arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT

        binding.editInformation.setOnClickListener {
            accountViewModel.openScreenEditAccount(accountID)
        }

        binding.deleteAccount.setOnClickListener {
            accountViewModel.showDialog(
                title = getString(R.string.deleteAccount),
                message = getString(R.string.warning_account),
                positive = getString(R.string.delete)
            ) {
                accountViewModel.deleteAccount(accountID)
            }
        }

        binding.totalHistory.setOnClickListener {
            accountViewModel.showDialog(
                title = getString(R.string.delete_history),
                message = getString(R.string.warning_history),
                positive = getString(R.string.delete)
            ) {
                accountViewModel.clearHistory(accountID)
            }
        }

        binding.totalFavorites.setOnClickListener {
            accountViewModel.showDialog(
                title = getString(R.string.delete_favorites),
                message = getString(R.string.warning_favorites),
                positive = getString(R.string.delete)
            ) {
                accountViewModel.clearFavorites(accountID)
            }
        }

        binding.totalSources.setOnClickListener {
            accountViewModel.showDialog(
                title = getString(R.string.delete_sources),
                message = getString(R.string.warning_sources),
                positive = getString(R.string.delete)
            ) {
                accountViewModel.clearSources(accountID)
            }
        }

        binding.logout.setOnClickListener {
            accountViewModel.showDialog(
                title = getString(R.string.confirm_logout),
                message = getString(R.string.warning_logout),
                positive = getString(R.string.logout)
            ) {
                accountViewModel.logout()
                accountViewModel.setBottomNavigationIcon()
            }
        }

        binding.saveHistorySwitch.setOnCheckedChangeListener { compoundButton, b ->
            accountViewModel.updateAccountSaveHistory(b)
        }

        binding.saveHistorySelectSwitch.setOnCheckedChangeListener { compoundButton, b ->
            accountViewModel.updateAccountSaveHistorySelect(b)
        }

        binding.showFavorites.setOnCheckedChangeListener { compoundButton, b ->
            accountViewModel.updateAccountShowListFavorite(b)
        }

        binding.switchDarkTheme.setOnClickListener {
            accountViewModel.setTheme(binding.switchDarkTheme.isChecked)
        }

        binding.fontSize.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }

        binding.imageUpload.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }

        binding.customizeCategory.setOnClickListener {
            accountViewModel.openScreenCustomizeCategory(
                arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT
            )
        }
        binding.notificationFirebase.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }
        binding.aboutInformation.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }

        binding.privacyPolicy.setOnClickListener {
            accountViewModel.openScreenWebView(getString(R.string.team_site))
        }

        binding.supportLinear.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }
    }

    fun toastDelete() {
        binding.root.showText(getString(R.string.textDelete))
    }

    fun recreateTheme() {
        activity?.recreate()
    }

    @SuppressLint("SetTextI18n")
    fun setAccountInfo(account: Account, themePrefs: Int) {

        binding.userName.text = account.userName
        binding.userEmail.text = account.email
        binding.totalFavoritesText.text =
            getString(R.string.total_favorites) + account.totalFavorites
        binding.totalHistoryText.text =
            getString(R.string.total_history) + account.totalHistory

        binding.totalSourcesText.text =
            getString(R.string.total_sources) + account.totalSources

        binding.saveHistorySwitch.isChecked = account.saveHistory
        binding.saveHistorySelectSwitch.isChecked = account.saveSelectHistory
        binding.showFavorites.isChecked = account.displayOnlySources

        binding.switchDarkTheme.isChecked = themePrefs != R.style.Theme_NewsAPI

        TransitionManager.beginDelayedTransition(binding.root)
        binding.nestedScrollAccount.show()
    }

    fun loading() {
        binding.nestedScrollAccount.hide()
    }

    fun setBottomNavigationIcon() {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetDefaultIcon()
    }

    fun showDialog(
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

        dialogBinding.negativeButton.setOnClickListener {
            dialog.dismiss()
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return accountViewModel.onBackPressedRouter()
    }

    companion object {
        fun getInstance(accountID: Int): AccountFragment {
            return AccountFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}
