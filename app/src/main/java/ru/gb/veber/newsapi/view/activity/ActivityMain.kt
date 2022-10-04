package ru.gb.veber.newsapi.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.rxjava3.core.Completable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.databinding.ActivityMainBinding
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.utils.*
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

interface EventOpenBehaviorNews {
    fun openNews(article: Article, accountId: Int)
}

class ActivityMain : MvpAppCompatActivity(), ViewMain, OpenScreen, EventLogoutAccountScreen,
    EventOpenBehaviorNews {

    private lateinit var binding: ActivityMainBinding
    private val navigator = AppNavigator(this, R.id.fragmentContainerMain)
    private var backStack = 1

    private lateinit var bSheetB: BottomSheetBehavior<ConstraintLayout>

    private val presenter: ActivityPresenter by moxyPresenter {
        ActivityPresenter(App.instance.router, SharedPreferenceAccount(),
            ArticleRepoImpl(App.instance.newsDb.articleDao()))
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

        bSheetB = BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            addBottomSheetCallback(callBackBehavior)
        }

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

    override fun onBackPressed() {
        if (bSheetB.state == BottomSheetBehavior.STATE_EXPANDED) {
            bSheetB.collapsed()
        } else {
            if (supportFragmentManager.fragments.last() !is AllNewsFragment && supportFragmentManager.fragments.last() !is WebViewFragment &&
                supportFragmentManager.fragments.last() !is EditAccountFragment
            ) {
                binding.bottomNavigationView.selectedItemId = R.id.allNews
            }

            Log.d("NavigateActivityBack", backStack.toString())
            if (supportFragmentManager.backStackEntryCount == 0 && backStack != 0) {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            } else {
                supportFragmentManager.fragments.forEach { fragment ->
                    Log.d("NavigateActivityBack",
                        "onBackPressed() forEach  fragment = $fragment")
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
    }

    override fun openMainScreen() {
        binding.bottomNavigationView.selectedItemId = R.id.allNews
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

    override fun bottomNavigationSetCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_person_24)
    }

    override fun bottomNavigationSetTitleCurrentAccount(checkLogin: String) {
        var item = binding.bottomNavigationView.menu.findItem(R.id.actionProfile)
        item.title = checkLogin
    }

    @SuppressLint("SetJavaScriptEnabled", "UseCompatLoadingForDrawables")
    override fun openNews(article: Article, accountId: Int) {

        presenter.saveArticle(article, accountId, false)

        with(binding) {
            webNews.hide()
            bSheetB.expanded()
        }


        with(article) {
            binding.imageViewAll.loadGlideNot(this.urlToImage)
            binding.dateNews.text = stringFromData(this.publishedAt).formatDateDay()
            binding.titleNews.text = this.title
            setSpanLinkDescription(this)
            binding.authorText.text = this.author
            binding.sourceText.text = this.source.name
        }

        binding.imageFavorites.setOnClickListener {
            binding.imageFavorites.setImageResource(R.drawable.ic_favorite_36_active)
            presenter.saveArticle(article, accountId, true)
        }

//        binding.descriptionNews.setOnClickListener { view ->
//
//            binding.bottomNavigationView.visibility = View.INVISIBLE
//            binding.nestedBehaviorMain.visibility = View.INVISIBLE
//            binding.webNews.visibility = View.VISIBLE
//            binding.webNews.webViewClient = webViewClient
//            binding.webNews.loadUrl(article.url)
//            binding.webNews.webChromeClient = WebChromeClient()
//            binding.webNews.settings.javaScriptEnabled = true
//            binding.webNews.clearHistory()
//            binding.webNews.clearCache(true)
//        }

    }

    private fun setSpanLinkDescription(it: Article) {
        SpannableStringBuilder(it.description).also { span ->
            span.setSpan(ImageSpan(this, R.drawable.ic_baseline_open_in_new_24),
                span.length - 1,
                span.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.descriptionNews.text = span
            span.removeSpan(span)
        }
    }

    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.webNews.visibility = View.VISIBLE
            binding.webNews.clearHistory()
            Log.d("webViewClient", "onPageFinished() called with: view = $view, url = $url")
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d("webViewClient",
                "onPageStarted() called with: view = $view, url = $url, favicon = $favicon")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            super.onReceivedError(view, request, error)
            Log.d("webViewClient",
                "onReceivedError() called with: view = $view, request = $request, error = $error")
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?,
        ) {
            Log.d("webViewClient",
                "onReceivedSslError() called with: view = $view, handler = $handler, error = $error")
        }
    }

    private val callBackBehavior = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                }
                BottomSheetBehavior.STATE_EXPANDED -> {

                }
                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (slideOffset <= 0.1) {
                binding.bottomNavigationView.show()
            } else {
                binding.bottomNavigationView.hide()
            }
        }
    }
}