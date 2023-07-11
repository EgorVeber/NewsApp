package ru.gb.veber.newsapi.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import ru.gb.veber.ui_common.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.ui_common.ALL_COUNTRY_VALUE
import ru.gb.veber.ui_common.API_KEY_EMPTY
import ru.gb.veber.ui_common.BUNDLE_ACCOUNT_ID_KEY
import ru.gb.veber.ui_common.KEY_THEME_DARK
import ru.gb.veber.ui_common.KEY_THEME_DEFAULT

//TODO нужен контекст подумать как вынетси в другой модуль и разделить на несколько класов
object PrefsAccountHelper {
    private const val PREFS_SETTINGS = "PREFS_SETTINGS"
    private const val PREFS_THEME_KEY = "PREFS_THEME_KEY"
    private const val PREFS_FIRST_START_KEY = "PREFS_FIRST_START_KEY"
    private const val PREFS_ACCOUNT_LOGIN_KEY = "PREFS_ACCOUNT_LOGIN_KEY"
    private const val PREFS_ACCOUNT_COUNTRY_KEY = "PREFS_ACCOUNT_COUNTRY_KEY"
    private const val PREFS_ACCOUNT_COUNTRY_CODE_KEY = "PREFS_ACCOUNT_COUNTRY_CODE_KEY"
    private const val PREFS_ACCOUNT_API_KEY_KEY = "PREFS_ACCOUNT_API_KEY_KEY"

    fun setTheme(key: Int) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putInt(PREFS_THEME_KEY, key).apply()
    }

    fun getThemePrefs(): Int {
        return when (getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getInt(PREFS_THEME_KEY, KEY_THEME_DEFAULT)
        ) {
            KEY_THEME_DEFAULT -> UiCoreStyle.Theme_NewsAPI
            KEY_THEME_DARK -> UiCoreStyle.Theme_NewsAPI_Dark
            else -> {
                UiCoreStyle.Theme_NewsAPI
            }
        }
    }

    fun getCheckFirstStartApp(): Boolean {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getBoolean(PREFS_FIRST_START_KEY, false)
    }

    fun setCheckFirstStartApp() {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putBoolean(PREFS_FIRST_START_KEY, true).apply()
    }

    fun getAccountID(): Int {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getInt(BUNDLE_ACCOUNT_ID_KEY, ACCOUNT_ID_DEFAULT)
    }

    fun setAccountID(id: Int) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putInt(BUNDLE_ACCOUNT_ID_KEY, id).apply()
    }

    fun getAccountLogin(): String {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(PREFS_ACCOUNT_LOGIN_KEY, ACCOUNT_LOGIN_DEFAULT)
            ?: ACCOUNT_LOGIN_DEFAULT
    }

    fun setAccountLogin(login: String) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putString(PREFS_ACCOUNT_LOGIN_KEY, login).apply()
    }

    fun getAccountCountry(): String {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(PREFS_ACCOUNT_COUNTRY_KEY, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountry(country: String) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putString(PREFS_ACCOUNT_COUNTRY_KEY, country).apply()
    }

    fun getAccountCountryCode(): String {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(PREFS_ACCOUNT_COUNTRY_CODE_KEY, ALL_COUNTRY_VALUE)
            ?: ALL_COUNTRY_VALUE
    }

    fun setAccountCountryCode(country: String) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putString(PREFS_ACCOUNT_COUNTRY_CODE_KEY, country).apply()
    }


    fun getArrayCountry(): HashMap<String, String> {
        val hashMap = hashMapOf<String, String>()
        val name = getAppContext().resources.getStringArray(UiCoreArrays.countryName)
        val code = getAppContext().resources.getStringArray(UiCoreArrays.countryCode)

        for (i in name.indices) {
            hashMap[name[i]] = code[i]
        }
        return hashMap
    }

    fun getArrayCategories(): Array<String> {
        return getAppContext().resources.getStringArray(UiCoreArrays.newsCategory)
    }

    fun setActiveKey(apiKey: String) {
        getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            Context.MODE_PRIVATE
        ).edit().putString(PREFS_ACCOUNT_API_KEY_KEY, apiKey).apply()
    }

    fun getActiveKey(): String {
        return getAppContext().getSharedPreferences(
            PREFS_SETTINGS,
            AppCompatActivity.MODE_PRIVATE
        ).getString(PREFS_ACCOUNT_API_KEY_KEY, API_KEY_EMPTY)
            ?: API_KEY_EMPTY
    }
}


