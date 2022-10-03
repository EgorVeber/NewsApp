package ru.gb.veber.newsapi.view.profile.account.settings

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.gb.veber.newsapi.model.Account

@StateStrategyType(AddToEndSingleStrategy::class)
interface EditAccountView : MvpView {
    fun setAccountDate(account: Account)
    fun loading()
    fun errorLoadingAccount()
    fun passwordIsValidate(it: CharSequence?)
    fun passwordNotValidate(it: CharSequence?)
    fun loginIsValidate(it: CharSequence?)
    fun loginNotValidate()
    fun emailRegisterIsValidate(it: CharSequence?)
    fun emailRegisterNotValidate()
    fun successUpdateAccount(userLogin:String)
    fun errorUpdateAccount()
    fun noChangeAccount()
}
