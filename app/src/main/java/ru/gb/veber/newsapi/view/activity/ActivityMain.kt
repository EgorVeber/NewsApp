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
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.utils.subscribeDefault


interface TestDate {
    fun getIdFragment(id: Int)
}
interface OpenScreen {
    fun openMainScreen()
}


class ActivityMain : MvpAppCompatActivity(), ViewMain, TestDate,OpenScreen {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)

    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(App.instance.router)
    }

    var words: MutableList<String> = mutableListOf()
    var map: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        NewsRepoImpl(NewsRetrofit.newsTopSingle).getSources().subscribeDefault().subscribe({
//            it.sources.forEach {
//                words.add(it.country!!)
//            }
//            for (i in 0 until words.size) {
//                if (map.containsKey(words[i]))
//                {
//                    map[words[i]] = map[words[i]]!! + 1
//                } else
//                {
//                    map[words[i]] = 1
//                }
//            }
//
//            Log.d("TAG", map.toList().sortedBy { it.second }.toMap().toSortedMap().toString())
//            Log.d("TAG", map.size.toString())
//        }, {
//            Log.d("TAG", it.localizedMessage)
//        })
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
                R.id.topNews -> {
                    presenter.openScreenNews()
                }
                R.id.sourcesNews -> {
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


        Log.d("@@@", "onBackPressed")
//        if (binding.vdfsdfsdfs.visibility == View.VISIBLE) {
//            binding.vdfsdfsdfs.visibility = View.GONE
//            return
//        }
        supportFragmentManager.fragments.forEach { fragment ->
            Log.d("@@@", "onBackPressed() forEach  fragment = $fragment")
            if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
                Log.d("@@@", "onBackPressed if")
                return
            }
        }
        Log.d("@@@", "onBackPressed forEach after")
        presenter.onBackPressedRouter()
    }

    override fun getIdFragment(id: Int) {
        binding.bottomNavigationView.selectedItemId = R.id.sourcesNews
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.allNews
    }
}