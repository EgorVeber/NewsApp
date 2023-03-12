package ru.gb.veber.newsapi.core

import android.app.Application
import ru.gb.veber.newsapi.di.AppComponent
import ru.gb.veber.newsapi.di.DaggerAppComponent
import ru.gb.veber.newsapi.di.moduls.AppModule

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(applicationContext)).build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}