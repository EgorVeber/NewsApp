package ru.gb.veber.newsapi.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import io.reactivex.rxjava3.core.Completable
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.utils.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.newsapi.utils.COUNTER_BACKSTACK
import ru.gb.veber.newsapi.utils.COUNTER_BADGE
import ru.gb.veber.newsapi.utils.ColorUtils.getDrawableRes
import ru.gb.veber.newsapi.utils.DELAY_BACK_STACK
import ru.gb.veber.newsapi.utils.extentions.showText
import ru.gb.veber.newsapi.view.profile.account.settings.CustomizeCategoryFragment
import ru.gb.veber.newsapi.view.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.view.search.searchnews.SearchNewsFragment
import ru.gb.veber.newsapi.view.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.view.webview.WebViewFragment
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

interface EventShareLink {
    fun shareLink(url: String)
}

class ActivityMain : AppCompatActivity(), OpenScreen, EventLogoutAccountScreen,
    EventAddingBadges, EventShareLink {

    private lateinit var binding: ActivityMainBinding
    private var backStack = COUNTER_BACKSTACK
    private var counterBadge = COUNTER_BADGE

    @Inject
    lateinit var sharedPreferenceAccount: SharedPreferenceAccount

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val activityMainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ActivityMainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)
        setTheme(sharedPreferenceAccount.getThemePrefs())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initViewModel()

        if (savedInstanceState != null) {
            activityMainViewModel.openScreenProfile()
        }

        activityMainViewModel.getAccountSettings()
        activityMainViewModel.getCheckFirstStartApp()


    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        //App.instance.navigationHolder.setNavigator(navigator)
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        //App.instance.navigationHolder.removeNavigator()
        navigatorHolder.removeNavigator()
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

        if (supportFragmentManager.fragments.last() !is SearchNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment &&
            supportFragmentManager.fragments.last() !is EditAccountFragment && supportFragmentManager.fragments.last() !is CustomizeCategoryFragment
        ) {
            binding.bottomNavigationView.selectedItemId = R.id.topNews
        }

        if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
            Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show()
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
        }.delay(DELAY_BACK_STACK, TimeUnit.MILLISECONDS).subscribe({
            backStack = 1
        }, {
        })
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.topNews
    }

    override fun bottomNavigationSetDefaultIcon() {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = ACCOUNT_LOGIN_DEFAULT
        item.icon = this.getDrawableRes(R.drawable.ic_baseline_person_add_alt_1_24)
    }

    override fun bottomNavigationSetCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
        item.icon = this.getDrawableRes(R.drawable.ic_baseline_person_24)
    }

    override fun bottomNavigationSetTitleCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
    }

    override fun addBadge() {
        counterBadge += 1
        var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.favoritesNews)
        badge.isVisible = true
        badge.number = counterBadge
    }

    override fun removeBadge() {
        counterBadge--
        if (counterBadge <= 0) {
            counterBadge = 0
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

    private fun initViewModel() {
        activityMainViewModel.subscribe().observe(this) { state ->
            when (state) {
                ActivityMainViewModel.ViewMainState.CompletableInsertSources -> {
                    completableInsertSources()
                }
                ActivityMainViewModel.ViewMainState.ErrorSourcesDownload -> {
                    errorSourcesDownload()
                }
                ActivityMainViewModel.ViewMainState.HideAllBehavior -> {
                    hideAllBehavior()
                }
                is ActivityMainViewModel.ViewMainState.OnCreateSetIconTitleAccount -> {
                    onCreateSetIconTitleAccount(state.accountLogin)
                }
            }
        }
    }

    private fun hideAllBehavior() {
        supportFragmentManager.fragments.forEach { fm ->
            if (fm is EventBehaviorToActivity) {
                (fm as EventBehaviorToActivity).setStateBehavior()
            }
        }
    }

    private fun init() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.topNews -> {
                    activityMainViewModel.openScreenNews()
                }
                R.id.searchNews -> {
                    activityMainViewModel.openScreenSearchNews()
                }
                R.id.actionSources -> {
                    activityMainViewModel.openScreenSources()
                }
                R.id.favoritesNews -> {
                    activityMainViewModel.openFavoritesScreen()
                    val badgeDrawable = binding.bottomNavigationView.getBadge(R.id.favoritesNews)
                    if (badgeDrawable != null) {
                        badgeDrawable.number = 0
                        counterBadge = 0
                        binding.bottomNavigationView.removeBadge(R.id.favoritesNews)
                    }
                }
                R.id.actionProfile -> {
                    activityMainViewModel.openScreenProfile()
                }
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.topNews
        binding.bottomNavigationView.setOnItemReselectedListener {

        }
    }

    private fun onCreateSetIconTitleAccount(accountLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = accountLogin
        item.icon = this.getDrawableRes(R.drawable.ic_baseline_person_24)
    }

    private fun completableInsertSources() {
        Toast.makeText(this, getString(R.string.source_loaded), Toast.LENGTH_SHORT).show()
    }

    private fun errorSourcesDownload() {
        binding.root.showText(getString(R.string.error_sources_download))
    }

    override fun shareLink(url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}