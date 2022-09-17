package ru.gb.veber.newsapi.view.activity

import android.os.Bundle
import android.util.Log
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.utils.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityMain : MvpAppCompatActivity(), ViewMain {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)

    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(App.instance.router)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//
//
//        Log.d("TAG",    request.substring(11,16))
////
//        Log.d("TAG", date.toString())
//        Log.d("TAG", date.formatHour())
//        Log.d("TAG", Date().toString())
//
//
//        var fmt = SimpleDateFormat("yyyy-MM-dd")
//    Log.d("TAG", fmt.format(date).equals(fmt.format(takeDate(-1))).toString())
//        Log.d("TAG", fmt.format(date).equals(fmt.format(Date())).toString())

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