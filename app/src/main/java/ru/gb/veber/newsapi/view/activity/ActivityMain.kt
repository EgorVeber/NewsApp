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
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.view.allnews.AllNewsFragment
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
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

class ActivityMain : MvpAppCompatActivity(), ViewMain, OpenScreen, EventLogoutAccountScreen {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)
    private var backStack = 1;

    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(App.instance.router, SharedPreferenceAccount())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.getAccountSettings()
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
                   // presenter.openScreenSources()
                    presenter.openFavoritesScreen()
                }
                R.id.actionProfile -> {
                    presenter.openScreenProfile()
                }
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.allNews
        binding.bottomNavigationView.setOnItemReselectedListener {

        }
    }

    override fun onCreateSetIconTitleAccount(accountLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = accountLogin
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24)
    }

    override fun onBackPressed() {

        if (supportFragmentManager.fragments.last() !is AllNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment &&
            supportFragmentManager.fragments.last() !is EditAccountFragment) {
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

    override fun bottomNavigationSetDefaultIcon() {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = ACCOUNT_LOGIN_DEFAULT
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_add_alt_1_24)
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
}