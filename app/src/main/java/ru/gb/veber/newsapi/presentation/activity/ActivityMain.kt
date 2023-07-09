package ru.gb.veber.newsapi.presentation.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import kotlinx.coroutines.flow.onEach
import ru.gb.veber.newsapi.common.PrefsAccountHelper
import ru.gb.veber.newsapi.common.UiCoreDrawable
import ru.gb.veber.newsapi.common.UiCoreId
import ru.gb.veber.newsapi.common.UiCoreStrings
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.presentation.activity.callbackhell.BackPressedListener
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventAddingBadges
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventLogoutAccountScreen
import ru.gb.veber.newsapi.presentation.activity.callbackhell.EventShareLink
import ru.gb.veber.newsapi.presentation.activity.callbackhell.OpenScreen
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.EditAccountFragment
import ru.gb.veber.newsapi.presentation.profile.account.settings.customize.CustomizeCategoryFragment
import ru.gb.veber.newsapi.presentation.searchnews.SearchNewsFragment
import ru.gb.veber.newsapi.presentation.topnews.fragment.EventBehaviorToActivity
import ru.gb.veber.newsapi.presentation.webview.WebViewFragment
import ru.gb.veber.ui_common.ACCOUNT_LOGIN_DEFAULT
import ru.gb.veber.ui_common.coroutine.flowStarted
import ru.gb.veber.ui_common.coroutine.launchDelay
import ru.gb.veber.ui_common.showText
import ru.gb.veber.ui_common.utils.ColorUtils.getDrawableRes
import ru.gb.veber.ui_core.databinding.ActivityMainBinding
import ru.gb.veber.ui_core.extentions.showSnackBar
import javax.inject.Inject

class ActivityMain : AppCompatActivity(), OpenScreen, EventLogoutAccountScreen,
    EventAddingBadges, EventShareLink {

    private lateinit var binding: ActivityMainBinding
    private var backStack = COUNTER_BACKSTACK
    private var counterBadge = COUNTER_BADGE

    @Inject
    lateinit var sharedPreferenceAccount: PrefsAccountHelper

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator = AppNavigator(this, UiCoreId.fragmentContainerMain)

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
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
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

        if (supportFragmentManager.fragments.lastOrNull() !is KeysManagementFragment && supportFragmentManager.fragments.last() !is SearchNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment &&
            supportFragmentManager.fragments.last() !is EditAccountFragment && supportFragmentManager.fragments.last() !is CustomizeCategoryFragment
        ) {
            binding.bottomNavigationView.selectedItemId = UiCoreId.topNews
        }

        if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
            Toast.makeText(this, getString(UiCoreStrings.press_again), Toast.LENGTH_SHORT).show()
        } else {
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment is BackPressedListener && fragment.onBackPressedRouter()) {
                    return
                }
            }
        }

        backStack = 0
        launchDelay(DELAY_BACK_STACK) { backStack = 1 }
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = UiCoreId.topNews
    }

    override fun bottomNavigationSetDefaultIcon() {
        val item = binding.bottomNavigationView.menu.findItem(UiCoreId.actionProfile)
        item.title = ACCOUNT_LOGIN_DEFAULT
        item.icon = getDrawableRes(UiCoreDrawable.ic_baseline_person_add_alt_1_24)
    }

    override fun bottomNavigationSetCurrentAccount(checkLogin: String) {
        val item = binding.bottomNavigationView.menu.findItem(UiCoreId.actionProfile)
        item.title = checkLogin
        item.icon = getDrawableRes(UiCoreDrawable.ic_baseline_person_24)
    }

    override fun bottomNavigationSetTitleCurrentAccount(checkLogin: String) {
        val item = binding.bottomNavigationView.menu.findItem(UiCoreId.actionProfile)
        item.title = checkLogin
    }

    override fun addBadge() {
        counterBadge += 1
        val badge = binding.bottomNavigationView.getOrCreateBadge(UiCoreId.favoritesNews)
        badge.isVisible = true
        badge.number = counterBadge
    }

    override fun removeBadge() {
        counterBadge--
        if (counterBadge <= 0) {
            counterBadge = 0
            val badgeDrawable = binding.bottomNavigationView.getBadge(UiCoreId.favoritesNews)
            if (badgeDrawable != null) {
                binding.bottomNavigationView.removeBadge(UiCoreId.favoritesNews)
            }
        } else {
            val badge = binding.bottomNavigationView.getOrCreateBadge(UiCoreId.favoritesNews)
            badge.isVisible = true
            badge.number = counterBadge
        }
    }

    private fun initViewModel() {
        activityMainViewModel.subscribe().onEach { state ->
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

                ActivityMainViewModel.ViewMainState.StartedState -> {}
            }
        }.flowStarted(lifecycleScope)

        activityMainViewModel.connectionFlow.onEach { statusNetwork ->
            if (!statusNetwork) this.showSnackBar(getString(UiCoreStrings.not_connection))
        }.flowStarted(lifecycleScope)
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
                UiCoreId.topNews -> {
                    activityMainViewModel.openScreenNews()
                }

                UiCoreId.searchNews -> {
                    activityMainViewModel.openScreenSearchNews()
                }

                UiCoreId.actionSources -> {
                    activityMainViewModel.openScreenSources()
                }
                UiCoreId.favoritesNews -> {
                    activityMainViewModel.openFavoritesScreen()
                    val badgeDrawable = binding.bottomNavigationView.getBadge(UiCoreId.favoritesNews)
                    if (badgeDrawable != null) {
                        badgeDrawable.number = 0
                        counterBadge = 0
                        binding.bottomNavigationView.removeBadge(UiCoreId.favoritesNews)
                    }
                }

                UiCoreId.actionProfile -> {
                    activityMainViewModel.openScreenProfile()
                }
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = UiCoreId.topNews
        binding.bottomNavigationView.setOnItemReselectedListener {

        }

        if (!isInternetAvailable(this)) {
            this.showSnackBar(getString(UiCoreStrings.not_connection))
        }
    }

    private fun onCreateSetIconTitleAccount(accountLogin: String) {
        val item = binding.bottomNavigationView.menu.findItem(UiCoreId.actionProfile)
        item.title = accountLogin
        item.icon = getDrawableRes(UiCoreDrawable.ic_baseline_person_24)
    }

    private fun completableInsertSources() {
        Toast.makeText(this, getString(UiCoreStrings.source_loaded), Toast.LENGTH_SHORT).show()
    }

    private fun errorSourcesDownload() {
        binding.root.showText(getString(UiCoreStrings.error_sources_download))
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


    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }

        return result
    }

    companion object {
        private const val COUNTER_BADGE = 0
        private const val COUNTER_BACKSTACK = 1
        const val DELAY_BACK_STACK = 2000L
    }
}