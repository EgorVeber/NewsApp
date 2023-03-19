package ru.gb.veber.newsapi.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.core.App
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var accountId by BundleInt(ACCOUNT_ID,0)

    private val profileViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)
        var accountId = arguments?.getInt(ACCOUNT_ID) ?: 0

        if (accountId != 0) {
            profileViewModel.openScreenProfile(accountId)
        } else {
            profileViewModel.openScreenAuthorization()
        }
    }

    companion object {
        fun getInstance(accountID: Int): ProfileFragment {
            return ProfileFragment().apply {
                this.accountId = accountID
            }
        }
    }
}
