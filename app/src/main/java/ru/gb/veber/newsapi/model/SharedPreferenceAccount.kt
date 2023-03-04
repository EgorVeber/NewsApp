package ru.gb.veber.newsapi.model

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.utils.*


object SharedPreferenceAccount {

    fun setTheme(key: Int) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putInt(KEY_THEME, key).apply()
    }

    fun getThemePrefs(): Int {
        return when (App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getInt(KEY_THEME, KEY_THEME_DEFAULT)
        ) {
            KEY_THEME_DEFAULT -> R.style.Theme_NewsAPI
            KEY_THEME_DARK -> R.style.Theme_NewsAPI_Dark
            else -> {
                R.style.Theme_NewsAPI
            }
        }
    }


    fun getCheckFirstStartApp(): Boolean {
        return App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getBoolean(FIRST_START_APP, false)
    }

    fun setCheckFirstStartApp() {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putBoolean(FIRST_START_APP, true).apply()
    }

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
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_LOGIN, ACCOUNT_LOGIN_DEFAULT)
            ?: ACCOUNT_LOGIN_DEFAULT
    }

    fun setAccountLogin(login: String) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_LOGIN, login).apply()
    }

    fun getAccountCountry(): String {
        return App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_COUNTRY, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountry(country: String) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_COUNTRY, country).apply()
    }

    fun getAccountCountryCode(): String {
        return App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_COUNTRY_CODE, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountryCode(country: String) {
        App.instance.applicationContext.getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_COUNTRY_CODE, country).apply()
    }


    fun getArrayCountry(): HashMap<String, String> {
        val hashMap = hashMapOf<String, String>()
        val name = App.instance.applicationContext.resources.getStringArray(R.array.countryName)
        val code = App.instance.applicationContext.resources.getStringArray(R.array.countryCode)

        for (i in name.indices) {
            hashMap[name[i]] = code[i]
        }
        return hashMap
    }
}


