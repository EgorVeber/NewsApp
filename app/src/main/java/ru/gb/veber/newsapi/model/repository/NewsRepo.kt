package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest

interface NewsRepo {
    fun getSources(): Single<SourcesRequest>
    fun getTopicalHeadlines(): Single<Articles>
}