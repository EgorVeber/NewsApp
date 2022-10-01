package ru.gb.veber.newsapi.view.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presenter.ProfilePresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class ProfileFragment : MvpAppCompatFragment(), ProfileView {


    private val presenter: ProfilePresenter by moxyPresenter {
        ProfilePresenter(App.instance.router)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var accountId = arguments?.getInt(ACCOUNT_ID) ?: 0

        if (accountId != 0) {
            presenter.openScreenProfile(accountId)
        } else {
            presenter.openScreenAuthorization()
        }
    }

    companion object {
        const val FILE_SETTINGS = "FILE_SETTINGS"
        const val ACCOUNT_ID = "ACCOUNT_ID"
        fun getInstance(accountID: Int): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACCOUNT_ID, accountID)
                }
            }
        }
    }
}