package ru.gb.veber.newsapi.view

import android.os.Bundle
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
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
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.actionNews
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
                    true
                }
                R.id.actionSources -> {
                    presenter.openScreenSources()
                    true
                }
                R.id.actionProfile -> {
                    presenter.openScreenProfile()
                    true
                }
                else -> {
                    true
                }
            }
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
        }
    }
}