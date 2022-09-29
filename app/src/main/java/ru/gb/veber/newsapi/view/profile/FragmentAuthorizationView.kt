package ru.gb.veber.newsapi.view.profile

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface FragmentAuthorizationView : MvpView {
    fun init()

    fun successRegister()
    fun errorRegister()
    fun successSignIn()
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


}