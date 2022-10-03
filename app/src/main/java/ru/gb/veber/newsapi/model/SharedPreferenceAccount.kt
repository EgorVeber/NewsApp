package ru.gb.veber.newsapi.model

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.utils.*


class SharedPreferenceAccount {

    fun getAccountID(): Int {
        return App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    }

    fun setAccountID(id: Int) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putInt(ACCOUNT_ID, id).apply()
    }

    fun getAccountLogin(): String {
        return App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_LOGIN, ACCOUNT_LOGIN_DEFAULT)?: ACCOUNT_LOGIN_DEFAULT
    }

    fun setAccountLogin(login:String) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_LOGIN, login).apply()
    }
}


