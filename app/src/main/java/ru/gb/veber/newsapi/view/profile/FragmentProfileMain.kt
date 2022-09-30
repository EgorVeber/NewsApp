package ru.gb.veber.newsapi.view.profile

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presenter.FragmentProfileMainPresenter
import ru.gb.veber.newsapi.view.activity.BackPressedListener

class FragmentProfileMain : MvpAppCompatFragment(), FragmentProfileMainView, BackPressedListener {



    private val presenter: FragmentProfileMainPresenter by moxyPresenter {
        FragmentProfileMainPresenter(App.instance.router)
    }



    override fun init() {

        val accountId = requireActivity()
            .getSharedPreferences(FILE_SETTINGS, AppCompatActivity.MODE_PRIVATE)
            .getInt(ACCOUNT_ID, 0)

        Log.d("ACCOUNT_ID", accountId.toString())
        if (accountId != 0) {
            presenter.openScreenProfile(accountId)
        } else {
            presenter.openScreenAuthorization()
        }
    }


    override fun onBackPressedRouter(): Boolean {
        return presenter.onBackPressedRouter()
    }

    companion object {

        const val FILE_SETTINGS = "FILE_SETTINGS"
        const val ACCOUNT_ID = "ACCOUNT_ID"

        fun getInstance(): FragmentProfileMain {
            return FragmentProfileMain()
        }
    }
}