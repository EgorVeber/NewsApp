package ru.gb.veber.newsapi.view.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
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

        binding.vdfsdfsdfs.setOnClickListener {
            TransitionSet().also { transition ->
                transition.duration = 500L
                transition.addTransition(Slide(Gravity.TOP))
                transition.addTransition(Fade())
                TransitionManager.beginDelayedTransition(binding.root, transition)
            }
            binding.vdfsdfsdfs.visibility = View.GONE
            binding.fragmentContainerMain.animate().alpha(1F).duration = 800
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_update -> {
                TransitionSet().also { transition ->
                    transition.duration = 500L
                    transition.addTransition(Fade())
                    transition.addTransition(Slide(Gravity.BOTTOM))
                    TransitionManager.beginDelayedTransition(binding.root, transition)
                }
                binding.vdfsdfsdfs.visibility = View.VISIBLE
                binding.fragmentContainerMain.animate().alpha(0F).duration = 800
            }
        }
        return super.onOptionsItemSelected(item)
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
        if (binding.vdfsdfsdfs.visibility == View.VISIBLE) {
            binding.vdfsdfsdfs.visibility = View.GONE
            return
        }
        supportFragmentManager.fragments.forEach { fragment ->
            Log.d("Back", "onBackPressed() called with: fragment = $fragment")
            if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
                return
            }
        }
        presenter.onBackPressedRouter()
    }
}