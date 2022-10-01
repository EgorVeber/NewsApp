package ru.gb.veber.newsapi.view.profile.authorization

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface AuthorizationView : MvpView {
    fun init()

    fun successRegister(id: Int)
    fun errorRegister()
    fun successSignIn(id: Int)
    fun errorSignIn()

    fun sendActivityOpenScreen()
    fun setRegisterAnim()
    fun setLoginAnim()

    fun loginNotValidate()
    fun loginIsValidate(charSequence: CharSequence?)

    fun passwordIsValidate(it: CharSequence?)
    fun passwordNotValidate(it: CharSequence?)

    fun loginRegisterIsValidate(it: CharSequence?)
    fun loginRegisterNotValidate()

    fun passwordRegisterIsValidate(it: CharSequence?)
    fun passwordRegisterNotValidate(it: CharSequence?)

    fun emailRegisterIsValidate(it: CharSequence?)
    fun emailRegisterNotValidate()

    fun emptyAccount()

   fun saveIdSharedPref(id: Int)


}