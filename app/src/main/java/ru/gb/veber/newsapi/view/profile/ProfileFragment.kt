package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.utils.ACCOUNT_ID
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}