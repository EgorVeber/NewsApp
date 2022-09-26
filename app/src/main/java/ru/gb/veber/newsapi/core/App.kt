package ru.gb.veber.newsapi.core

import android.app.Application
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.model.database.NewsDataBase

class App : Application() {

    private val cicerone: Cicerone<Router> by lazy { Cicerone.create() }
    val router = cicerone.router
    val navigationHolder = cicerone.getNavigatorHolder()

    val newsDb: NewsDataBase by lazy {
        NewsDataBase.createDb(this)
    }

    // private lateinit var connetivityListener: ConnetivityListener

    //  val database: GithubDB by lazy { GithubDB.create(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        connetivityListener = ConnetivityListener(
//            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        )
    }

//    fun getConnectObservable() = connetivityListener.status()
//    fun getConnectSingle() = connetivityListener.statusSingle()

    companion object {
        lateinit var instance: App
    }
}