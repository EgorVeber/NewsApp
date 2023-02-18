package ru.gb.veber.newsapi.view.profile.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.AccountFragmentBinding
import ru.gb.veber.newsapi.databinding.ConfirmDialogBinding
import ru.gb.veber.newsapi.model.Account
import ru.gb.veber.newsapi.presenter.AccountPresenter
import ru.gb.veber.newsapi.utils.*
import ru.gb.veber.newsapi.view.activity.BackPressedListener
import ru.gb.veber.newsapi.view.activity.EventLogoutAccountScreen

class AccountFragment : MvpAppCompatFragment(), AccountView, BackPressedListener {

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!

    private val presenter: AccountPresenter by moxyPresenter {
        AccountPresenter().apply {
            App.instance.appComponent.inject(this)
        }
    }

    override fun toastDelete() {
        binding.root.showText(getString(R.string.textDelete))
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
            presenter.getAccountSettings(this)
        }
    }

    override fun init() {

        binding.logout.setOnClickListener {
            presenter.showDialog(
                title = getString(R.string.confirm_logout),
                message = getString(R.string.warning_logout),
                positive = getString(R.string.logout)
            ) {
                presenter.logout()
                presenter.setBottomNavigationIcon()
            }
        }

        binding.editInformation.setOnClickListener {
            presenter.openScreenEditAccount(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        }

        binding.deleteAccount.setOnClickListener {
            presenter.showDialog(
                title = getString(R.string.deleteAccount),
                message = getString(R.string.warning_account),
                positive = getString(R.string.delete)
            ) {
                presenter.deleteAccount(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
            }
        }

        binding.totalHistory.setOnClickListener {
            presenter.showDialog(
                title = getString(R.string.delete_history),
                message = getString(R.string.warning_history),
                positive = getString(R.string.delete)
            ) {
                presenter.clearHistory(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
            }
        }

        binding.totalFavorites.setOnClickListener {
            presenter.showDialog(
                title = getString(R.string.delete_favorites),
                message = getString(R.string.warning_favorites),
                positive = getString(R.string.delete)
            ) {
                presenter.clearFavorites(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
            }
        }

        binding.totalSources.setOnClickListener {
            presenter.showDialog(
                title = getString(R.string.delete_sources),
                message = getString(R.string.warning_sources),
                positive = getString(R.string.delete)
            ) {
                presenter.clearSources(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
            }
        }

        binding.saveHistorySwitch.setOnCheckedChangeListener { compoundButton, b ->
            presenter.updateAccountSaveHistory(b)
        }

        binding.saveHistorySelectSwitch.setOnCheckedChangeListener { compoundButton, b ->
            presenter.updateAccountSaveHistorySelect(b)
        }

        binding.showFavorites.setOnCheckedChangeListener { compoundButton, b ->
            presenter.updateAccountShowListFavorite(b)
        }

        binding.switchDarkTheme.setOnClickListener {
            presenter.setTheme(binding.switchDarkTheme.isChecked)
        }

        binding.fontSize.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }

        binding.imageUpload.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }

        binding.customizeCategory.setOnClickListener {
            presenter.openScreenCustomizeCategory(
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
            it.showText(getString(R.string.notAvailable))
        }
        binding.supportLinear.setOnClickListener {
            it.showText(getString(R.string.notAvailable))
        }
    }

    override fun recreateTheme() {
        activity?.recreate()
    }

    @SuppressLint("SetTextI18n")
    override fun setAccountInfo(account: Account, themePrefs: Int) {

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

    override fun loading() {
        binding.nestedScrollAccount.hide()
    }

    override fun setBottomNavigationIcon() {
        (requireActivity() as EventLogoutAccountScreen).bottomNavigationSetDefaultIcon()
    }

    override fun showDialog(
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
    override fun clearHistory() {
        binding.totalHistoryText.text = getString(R.string.total_history) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearHistory), "", {})
    }

    @SuppressLint("SetTextI18n")
    override fun clearFavorites() {
        binding.totalFavoritesText.text = getString(R.string.total_favorites) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearFavorites), "", {})
    }

    @SuppressLint("SetTextI18n")
    override fun clearSources() {
        binding.totalSourcesText.text = getString(R.string.total_sources) + " 0"
        binding.nestedScrollAccount.showSnackBarError(getString(R.string.clearSources), "", {})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
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