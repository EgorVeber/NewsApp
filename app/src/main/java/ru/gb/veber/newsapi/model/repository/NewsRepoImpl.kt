package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.gb.veber.newsapi.model.SourcesRequest

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {
    override fun getSources(): Single<SourcesRequest> =
        newsApi.getSources("33804c62820c4b94864ee739f17a8d08")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}