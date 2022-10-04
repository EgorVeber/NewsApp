package ru.gb.veber.newsapi.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import ru.gb.veber.newsapi.core.*
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.SharedPreferenceAccount
import ru.gb.veber.newsapi.model.repository.room.ArticleRepoImpl
import ru.gb.veber.newsapi.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.utils.ERROR_DB
import ru.gb.veber.newsapi.utils.mapToArticleDbEntity
import ru.gb.veber.newsapi.view.activity.ViewMain


class ActivityPresenter(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val articleRepoImpl: ArticleRepoImpl,
) : MvpPresenter<ViewMain>() {
    private var currentArticle = 0
    override fun onFirstViewAttach() {
        Log.d("TAG", "onFirstViewAttach() called")
        viewState.init()
        super.onFirstViewAttach()
    }

    fun openScreenNews() {
        router.replaceScreen(TopNewsViewPagerScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenSources() {
        router.replaceScreen(SourcesScreen)
    }

    fun openScreenProfile() {
        router.replaceScreen(ProfileScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenSearchNews() {
        router.replaceScreen(SearchNewsScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenAllNews() {
        router.replaceScreen(AllNewsScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun onBackPressedRouter() {
        Log.d("Navigate", " router.exit() ActivityPresenter")
        router.exit()
    }

    fun getAccountSettings() {
        if (sharedPreferenceAccount.getAccountID() != ACCOUNT_ID_DEFAULT) {
            viewState.onCreateSetIconTitleAccount(sharedPreferenceAccount.getAccountLogin())
        }
    }

    fun openFavoritesScreen() {
        router.replaceScreen(FavoritesViewPagerScreen(sharedPreferenceAccount.getAccountID()))
    }

    fun openScreenWebView(url: String) {
        // router.navigateTo(WebViewScreen(url))
    }

    fun saveArticle(article: Article, accountId: Int, isLike: Boolean) {
        if (accountId != ACCOUNT_ID_DEFAULT) {
            //Проверка на сохранение истории
            articleRepoImpl.insertArticle(mapToArticleDbEntity(article.apply {
                isHistory = true
                if (isLike) isFavorites = true
            }, accountId)).andThen(
                articleRepoImpl.getLastArticle()
            ).subscribe({
                currentArticle = it.id
                Log.d(ERROR_DB, it.toString())
            }, {
                Log.d(ERROR_DB, it.localizedMessage)
            })
        }
    }

//    fun saveArticleLike(article: Article, accountId: Int) {
//        if (accountId != ACCOUNT_ID_DEFAULT) {
//            if (currentArticle != 0) {
//                var item = mapToArticleDbEntity(article, accountId)
//                item.isFavorites = true
//                item.id = currentArticle
//                articleRepoImpl.updateArticle(item).subscribe({
//                    //viewState.successInsertArticle()
//                    Log.d(ERROR_DB, "successInsertArticle")
//                }, {
//                    Log.d(ERROR_DB, it.localizedMessage)
//                })
//            } else {
//                saveArticle(article, accountId)
//            }
//        }
//    }
}