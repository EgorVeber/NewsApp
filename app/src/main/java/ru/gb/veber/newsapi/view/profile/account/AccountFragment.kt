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
import ru.gb.veber.newsapi.databinding.DialogDeleteAccountBinding
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
            presenter.logout()
            presenter.setBottomNavigationIcon()
        }

        binding.editInformation.setOnClickListener {
            presenter.openScreenEditAccount(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        }

        binding.deleteAccount.setOnClickListener {
            presenter.showDialog()
        }

        binding.totalHistory.setOnClickListener {
            presenter.clearHistory(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        }

        binding.totalFavorites.setOnClickListener {
            presenter.clearFavorites(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
        }

        binding.totalSources.setOnClickListener {
            presenter.clearSources(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
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
            it.showText(getString(R.string.notAvailable))
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
            getString(R.string.totalSources) + account.totalSources

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

    override fun showDialog() {
        var dialogBinding = DialogDeleteAccountBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setTitle(getString(R.string.deleteAccount))
            .setMessage(getString(R.string.warningAccount))
            .setView(dialogBinding.root)
            .create()
        dialog.show()

        dialogBinding.negativeDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.positiveDelete.setOnClickListener {
            presenter.deleteAccount(arguments?.getInt(ACCOUNT_ID) ?: ACCOUNT_ID_DEFAULT)
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
        binding.totalSourcesText.text = getString(R.string.totalSources) + " 0"
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