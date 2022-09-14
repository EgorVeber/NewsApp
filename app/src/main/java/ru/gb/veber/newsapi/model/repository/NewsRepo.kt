package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.data.ArticlesDTO
import ru.gb.veber.newsapi.model.data.SourcesRequestDTO

interface NewsRepo {

    fun getSources(): Single<SourcesRequestDTO>

    //Не эффективно
    fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO>
    fun getTopicalHeadlinesCountryKeyWord(country: String, keyWord: String): Single<ArticlesDTO>//Не эффективно
    fun getTopicalHeadlinesCountryCategory(country: String, category: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesCategoryKeyWord(category: String, keyWord: String): Single<ArticlesDTO>    //Не эффективно
    fun getTopicalHeadlinesSourcesKeyWord(keyWord: String, sources: String): Single<ArticlesDTO> //Не эффективно
    fun getTopicalHeadlinesCountry(country: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesCategory(category: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesKeyWord(keyWord: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesSources(sources: String): Single<ArticlesDTO>

    fun getEverything(keyWord: String): Single<ArticlesDTO>
}