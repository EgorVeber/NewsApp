package ru.gb.veber.newsapi.view.profile

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentAuthorizationView:MvpView {
    fun init()
    fun success()
    fun error()
    fun sendActivityOpenScreen()
    fun setRegisterAnim()
    fun setLoginAnim()
    fun loginIsValidate(charSequence: CharSequence?)
    fun loginNotValidate()
     fun passwordIsValidate(it: CharSequence?)
     fun passwordNotValidate(it: CharSequence?)
}