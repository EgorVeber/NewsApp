package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.data.Articles
import ru.gb.veber.newsapi.model.data.SourcesRequest

interface NewsRepo {

    fun getSources(): Single<SourcesRequest>

    //Не эффективно
    fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<Articles>
    fun getTopicalHeadlinesCountryKeyWord(country: String, keyWord: String): Single<Articles>//Не эффективно
    fun getTopicalHeadlinesCountryCategory(country: String, category: String): Single<Articles>
    fun getTopicalHeadlinesCategoryKeyWord(category: String, keyWord: String): Single<Articles>    //Не эффективно
    fun getTopicalHeadlinesSourcesKeyWord(keyWord: String, sources: String): Single<Articles> //Не эффективно
    fun getTopicalHeadlinesCountry(country: String): Single<Articles>
    fun getTopicalHeadlinesCategory(category: String): Single<Articles>
    fun getTopicalHeadlinesKeyWord(keyWord: String): Single<Articles>
    fun getTopicalHeadlinesSources(sources: String): Single<Articles>

    fun getEverything(keyWord: String): Single<Articles>
}