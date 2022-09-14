package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {

    override fun getSources(): Single<SourcesRequest> =
        newsApi.getSources()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getTopicalHeadlines(): Single<Articles> =
        newsApi.getTopicalHeadlines( "ru", "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}