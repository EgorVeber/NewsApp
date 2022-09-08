package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.SourcesRequest

interface NewsRepo {
    fun getSources(): Single<SourcesRequest>
}