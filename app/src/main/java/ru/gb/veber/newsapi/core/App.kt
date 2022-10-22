package ru.gb.veber.newsapi.core

import android.app.Application
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.di.AppComponent
import ru.gb.veber.newsapi.di.AppModule
import ru.gb.veber.newsapi.di.DaggerAppComponent
import ru.gb.veber.newsapi.model.database.NewsDataBase
import javax.inject.Singleton

class App : Application() {

    private val cicerone: Cicerone<Router> by lazy { Cicerone.create() }
    val router = cicerone.router
    val navigationHolder = cicerone.getNavigatorHolder()

    lateinit var appComponent: AppComponent

    val newsDb: NewsDataBase by lazy {
        NewsDataBase.createDb(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
    }

    companion object {
        lateinit var instance: App
    }
}