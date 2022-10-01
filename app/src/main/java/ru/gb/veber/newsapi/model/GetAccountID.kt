package ru.gb.veber.newsapi.model

import androidx.appcompat.app.AppCompatActivity
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.view.profile.ProfileFragment


fun getAccountID(): Int {
    return App.instance.applicationContext.getSharedPreferences(ProfileFragment.FILE_SETTINGS,
        AppCompatActivity.MODE_PRIVATE)
        .getInt(ProfileFragment.ACCOUNT_ID, 0)
}
