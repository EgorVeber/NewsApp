package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.repository.RoomRepoImpl
import ru.gb.veber.newsapi.view.profile.FragmentAuthorizationView
import java.util.*

class FragmentAuthorizationPresenter(
    private val router: Router,
    private val roomRepoImpl: RoomRepoImpl,
) :
    MvpPresenter<FragmentAuthorizationView>() {

    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        super.onFirstViewAttach()
    }

    fun onBackPressedRouter(): Boolean {
        Log.d("@@@", "onBackPressedRouter() ActivityPresenter")
        router.exit()
        return true
    }

    fun createAccount(username: String, email: String, password: String) {
        roomRepoImpl.createAccount(AccountDbEntity(0, username, password, email, Date().toString()))
            .subscribe({
                viewState.success()
            }, {

                Log.d("ERROR_DB",it.localizedMessage)
            })
    }

    fun openScreenProfile() {
        router.navigateTo(FragmentProfileScreen)
    }
}