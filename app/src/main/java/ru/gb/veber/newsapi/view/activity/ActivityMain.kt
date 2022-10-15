package ru.gb.veber.newsapi.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.terrakok.cicerone.androidx.AppNavigator
import io.reactivex.rxjava3.core.Completable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.network.NewsRetrofit
import ru.gb.veber.newsapi.model.repository.NewsRepoImpl
import ru.gb.veber.newsapi.model.repository.room.SourcesRepoImpl
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.view.search.searchnews.AllNewsFragment
import ru.gb.veber.newsapi.view.topnews.pageritem.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.webview.WebViewFragment
import java.util.concurrent.TimeUnit


interface OpenScreen {
    fun openMainScreen()
}

interface EventLogoutAccountScreen {
    fun bottomNavigationSetDefaultIcon()
    fun bottomNavigationSetCurrentAccount(checkLogin: String)
    fun bottomNavigationSetTitleCurrentAccount(checkLogin: String)
}

interface EventAddingBadges {
    fun addBadge()
    fun removeBadge()
}

class ActivityMain : MvpAppCompatActivity(), ViewMain, OpenScreen, EventLogoutAccountScreen,
    EventAddingBadges {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)
    private var backStack = 1
    private var counterBadge = 0


    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(NewsRepoImpl(NewsRetrofit.newsTopSingle),
            App.instance.router,
            SourcesRepoImpl(App.instance.newsDb.sourcesDao()),
            SharedPreferenceAccount())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.getAccountSettings()

//        AccountSourcesRepoImpl(App.instance.newsDb.accountSourcesDao()).getLikeSourcesFromAccount(3)
//            .subscribe({
//                it.forEach {
//                    Log.d(ERROR_DB, it.toString())
//                }
//            }, {
//                Log.d(ERROR_DB, "AccountSources" + it.localizedMessage)
//            })
    }

    override fun onResume() {
        super.onResume()
        presenter.getCheckFirstStartApp()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        App.instance.navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        App.instance.navigationHolder.removeNavigator()
    }

    override fun hideAllBehavior() {
        supportFragmentManager.fragments.forEach { fm ->
            if (fm is EventBehaviorToActivity) {
                (fm as EventBehaviorToActivity).setStateBehavior()
            }
        }
    }

    override fun init() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.topNews -> {
                    presenter.openScreenNews()
                }
                R.id.searchNews -> {
                    presenter.openScreenSearchNews()
                }
                R.id.actionSources -> {
                    presenter.openScreenSources()
                }
                R.id.favoritesNews -> {
                    presenter.openFavoritesScreen()
                    val badgeDrawable = binding.bottomNavigationView.getBadge(R.id.favoritesNews)
                    if (badgeDrawable != null) {
                        badgeDrawable.number = 0
                        counterBadge = 0
                        binding.bottomNavigationView.removeBadge(R.id.favoritesNews)
                    }
                }
                R.id.actionProfile -> {
                    presenter.openScreenProfile()
                }
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.topNews
        binding.bottomNavigationView.setOnItemReselectedListener {

        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (supportFragmentManager.fragments.last() is WebViewFragment) {
                return@forEach
            }
            if (it is EventBehaviorToActivity) {
                if ((it as EventBehaviorToActivity).getStateBehavior() == 3) {
                    (it as EventBehaviorToActivity).setStateBehavior()
                    return
                }
            }
        }

        if (supportFragmentManager.fragments.last() !is AllNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment &&
            supportFragmentManager.fragments.last() !is EditAccountFragment
        ) {
            binding.bottomNavigationView.selectedItemId = R.id.topNews
        }

        if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
        } else {
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
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


//11
//        supportFragmentManager.fragments.forEach {
//            if (it is EventBehaviorToActivity) {
//                if ((it as EventBehaviorToActivity).getStateBehavior() == 3) {
//                    (it as EventBehaviorToActivity).setStateBehavior()
//                    Log.d("supportFragmentManager", "onBackPressed() called")
//                    return@forEach
//                } else if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
//                    Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
//                    Log.d("supportFragmentManager", "count  = " +
//                            supportFragmentManager.backStackEntryCount.toString() + " backStack = " + backStack.toString())
//                } else {
//                    if (it is BackPressedListener && it.onBackPressedRouter()) {
//                        return@forEach
//                    }
//                }
//            }
//        }
////
////        if (supportFragmentManager.fragments.last() !is WebViewFragment &&
////            supportFragmentManager.fragments.last() !is EditAccountFragment &&
////            supportFragmentManager.fragments.last() !is AllNewsFragment
////        ) {
////            binding.bottomNavigationView.selectedItemId = R.id.topNews
////            Log.d("supportFragmentManager", "last")
////        }
//
//
//        backStack = 0
//        Completable.create {
//            it.onComplete()
//        }.delay(2000L, TimeUnit.MILLISECONDS).subscribe({
//            backStack = 1
//        }, {
//        })
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.topNews
    }

    override fun bottomNavigationSetDefaultIcon() {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = ACCOUNT_LOGIN_DEFAULT
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_add_alt_1_24)
    }

    override fun onCreateSetIconTitleAccount(accountLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = accountLogin
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24)
    }

    override fun completableInsertSources() {
        Toast.makeText(this, "Check out the sources section", Toast.LENGTH_SHORT).show()
    }

    override fun bottomNavigationSetCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24)
    }

    override fun bottomNavigationSetTitleCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
    }

    override fun addBadge() {
        Log.d("counterBadge", counterBadge.toString())
        counterBadge += 1
        var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.favoritesNews)
        badge.isVisible = true
        badge.number = counterBadge
    }

    override fun removeBadge() {
        counterBadge--
        if (counterBadge <= 0) {
            counterBadge = 0
            Log.d("counterBadge", counterBadge.toString())
            val badgeDrawable = binding.bottomNavigationView.getBadge(R.id.favoritesNews)
            if (badgeDrawable != null) {
                binding.bottomNavigationView.removeBadge(R.id.favoritesNews)
            }
        } else {
            var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.favoritesNews)
            badge.isVisible = true
            badge.number = counterBadge
        }
    }
}