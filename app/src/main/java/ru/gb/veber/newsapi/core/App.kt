package ru.gb.veber.newsapi.core

import android.app.Application
import ru.gb.veber.newsapi.di.AppComponent
import ru.gb.veber.newsapi.di.AppModule
import ru.gb.veber.newsapi.di.DaggerAppComponent
import ru.gb.veber.newsapi.model.database.NewsDataBase

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(applicationContext)).build()
    }

    //lateinit var appComponent: AppComponent

//    val newsDb: NewsDataBase by lazy {
//        NewsDataBase.createDb(this)
//    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // appComponent = DaggerAppComponent.create()
//        appComponent = DaggerAppComponent.builder()
//            .appModule(AppModule(applicationContext))
//            .build()
    }
}