package ru.gb.veber.newsapi.view.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.github.terrakok.cicerone.androidx.AppNavigator
import io.reactivex.rxjava3.core.Completable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.view.allnews.AllNewsFragment
import ru.gb.veber.newsapi.view.profile.ProfileFragment
import ru.gb.veber.newsapi.view.webview.WebViewFragment
import java.util.concurrent.TimeUnit


interface OpenScreen {
    fun openMainScreen()
}


class ActivityMain : MvpAppCompatActivity(), ViewMain, OpenScreen {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)
    private var backStack = 1;


    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(App.instance.router)
    }

    var words: MutableList<String> = mutableListOf()
    var map: HashMap<String, Int> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//       getSharedPreferences(ProfileFragment.FILE_SETTINGS,
//            Context.MODE_PRIVATE).edit().putInt(
//           ProfileFragment.ACCOUNT_ID, 0).apply()


//      RoomRepoImpl(App.instance.newsDb.accountsDao()).deleteAllAccount().subscribe({
//          Log.d("TAG", "delete")
//      },{
//
//      })
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
//                binding.vdfsdfsdfs.visibility = View.VISIBLE
//                binding.fragmentContainerMain.animate().alpha(0F).duration = 800
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
                    Log.d("TAG", "init() called")
                    presenter.openScreenNews()
                }
                R.id.searchNews -> {
                    presenter.openScreenSearchNews()
                }
                R.id.allNews -> {
                    presenter.openScreenAllNews()
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
        // changeMenuItemTextAndIcon()
        binding.bottomNavigationView.selectedItemId = R.id.allNews
        binding.bottomNavigationView.setOnItemReselectedListener {

        }
    }

    fun changeMenuItemTextAndIcon() {
        val item: MenuItem = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = "JS"
        //item.icon = ContextCompat.getDrawable(activity, R.drawable.ic_barcode)
    }

    override fun onBackPressed() {

        if (supportFragmentManager.fragments.last() !is AllNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment) {
            binding.bottomNavigationView.selectedItemId = R.id.allNews
        }

        Log.d("NavigateActivityBack", backStack.toString())
        if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
        } else {
            supportFragmentManager.fragments.forEach { fragment ->
                Log.d("NavigateActivityBack", "onBackPressed() forEach  fragment = $fragment")
                if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
                    Log.d("NavigateActivityBack", "onBackPressed if")
                    return
                }
            }
        }

        backStack = 0
        Completable.create {
            it.onComplete()
        }.delay(2000L, TimeUnit.MILLISECONDS).subscribe({
            backStack = 1
        }, {
        })
        //presenter.onBackPressedRouter()
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.allNews
    }
}