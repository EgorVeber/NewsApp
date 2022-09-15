package ru.gb.veber.newsapi.view

import android.os.Bundle
import android.util.Log
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.NewsRetrofit
import ru.gb.veber.newsapi.presenter.MainPresenter
import ru.gb.veber.newsapi.presenter.ViewMain

class ActivityMain : MvpAppCompatActivity(), ViewMain {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)

    private val presenter: MainPresenter by moxyPresenter {
        MainPresenter(App.instance.router)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var api = NewsRepoImpl(NewsRetrofit.newsTopSingle)
        api.getEverythingKeyWordSearchInSources(sources = "engadget", searchIn = "title", q = "bitcoin", sortBy = "publishedAt", from = "2022-08-25",to= "2022-09-20").subscribe({
            Log.d("@@NEWS", it.totalResults.toString())
            it.articles.forEach {
                Log.d("@@NEWS", " Заголовок:${it.title}   Автор:${it.author}")
            }
        }, {
            Log.d("@@NEWS", it.localizedMessage)
        })

        if (savedInstanceState == null) {
            // binding.bottomNavigationView.selectedItemId = R.id.actionNews
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        App.instance.navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        App.instance.navigationHolder.removeNavigator()
    }


    override fun init() {

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.actionNews -> {
                    presenter.openScreenNews()
                }
                R.id.actionSources -> {
                    presenter.openScreenSources()
                }
                R.id.actionProfile -> {
                    presenter.openScreenProfile()
                }
            }
            true
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach { fragment ->
            //  Log.d("Back", "onBackPressed() called with: fragment = $fragment")
            if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
                return
            }
        }
        presenter.onBackPressedRouter()
    }
}