package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gb.veber.newsapi.model.data.ArticlesDTO
import ru.gb.veber.newsapi.model.data.SourcesRequestDTO
import ru.gb.veber.newsapi.utils.EVERYTHING

interface NewsRepo {


    //TOP_HEADLINES_SOURCES
    fun getSources(): Single<SourcesRequestDTO>

    //TOP_HEADLINES
    fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO>    //Не эффективно

    fun getTopicalHeadlinesCountryKeyWord(
        country: String,
        keyWord: String,
    ): Single<ArticlesDTO>//Не эффективно

    fun getTopicalHeadlinesCountryCategory(country: String, category: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesCategoryKeyWord(
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO>    //Не эффективно

    fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<ArticlesDTO> //Не эффективно

    fun getTopicalHeadlinesCountry(country: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesCategory(category: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesKeyWord(keyWord: String): Single<ArticlesDTO>
    fun getTopicalHeadlinesSources(sources: String): Single<ArticlesDTO>


    //EVERYTHING


    fun getEverythingKeyWordSearchInSources(
        sources: String,
        q: String? = null,
        searchIn: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
    ): Single<ArticlesDTO>

    fun getEverythingKeyWordSearchIn(
        q: String,
        searchIn: String? = null,
        language: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        ): Single<ArticlesDTO>


}