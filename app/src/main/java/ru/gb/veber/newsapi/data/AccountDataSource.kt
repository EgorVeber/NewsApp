package ru.gb.veber.newsapi.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.getAppContext
import ru.gb.veber.newsapi.common.utils.ACCOUNT_COUNTRY
import ru.gb.veber.newsapi.common.utils.ACCOUNT_COUNTRY_CODE
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.ACCOUNT_LOGIN
import ru.gb.veber.newsapi.common.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.common.utils.ALL_COUNTRY_VALUE
import ru.gb.veber.newsapi.common.utils.API_KEY_EMPTY
import ru.gb.veber.newsapi.common.utils.API_KEY_KEY
import ru.gb.veber.newsapi.common.utils.FILE_SETTINGS
import ru.gb.veber.newsapi.common.utils.FIRST_START_APP
import ru.gb.veber.newsapi.common.utils.KEY_THEME
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DARK
import ru.gb.veber.newsapi.common.utils.KEY_THEME_DEFAULT
import ru.gb.veber.newsapi.core.App

object AccountDataSource {

    fun setTheme(key: Int) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putInt(KEY_THEME, key).apply()
    }

    fun getThemePrefs(): Int {
        return when (getAppContext().getSharedPreferences(FILE_SETTINGS,
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
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getBoolean(FIRST_START_APP, false)
    }

    fun setCheckFirstStartApp() {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putBoolean(FIRST_START_APP, true).apply()
    }

    fun getAccountID(): Int {
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)
    }

    fun setAccountID(id: Int) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putInt(ACCOUNT_ID, id).apply()
    }

    fun getAccountLogin(): String {
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_LOGIN, ACCOUNT_LOGIN_DEFAULT)
            ?: ACCOUNT_LOGIN_DEFAULT
    }

    fun setAccountLogin(login: String) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_LOGIN, login).apply()
    }

    fun getAccountCountry(): String {
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_COUNTRY, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountry(country: String) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_COUNTRY, country).apply()
    }

    fun getAccountCountryCode(): String {
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(ACCOUNT_COUNTRY_CODE, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountryCode(country: String) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(ACCOUNT_COUNTRY_CODE, country).apply()
    }


    fun getArrayCountry(): HashMap<String, String> {
        val hashMap = hashMapOf<String, String>()
        val name = getAppContext().resources.getStringArray(R.array.countryName)
        val code = getAppContext().resources.getStringArray(R.array.countryCode)

        for (i in name.indices) {
            hashMap[name[i]] = code[i]
        }
        return hashMap
    }

    fun getArrayCategories(): Array<String> {
        return getAppContext().resources.getStringArray(R.array.newsCategory)
    }

    fun setActiveKey(apiKey: String) {
        getAppContext().getSharedPreferences(FILE_SETTINGS,
            Context.MODE_PRIVATE).edit().putString(API_KEY_KEY, apiKey).apply()
    }

    fun getActiveKey(): String {
        return getAppContext().getSharedPreferences(FILE_SETTINGS,
            AppCompatActivity.MODE_PRIVATE).getString(API_KEY_KEY, API_KEY_EMPTY)
            ?: API_KEY_EMPTY
    }
}


