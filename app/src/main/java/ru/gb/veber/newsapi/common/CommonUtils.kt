package ru.gb.veber.newsapi.common

import android.content.Context
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.di.AppComponent

fun getAppComponent(): AppComponent = App.instance.appComponent

fun getAppContext(): Context = App.instance.applicationContext

