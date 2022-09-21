package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticlesDTO
import ru.gb.veber.newsapi.model.SourcesRequestDTO

interface NewsRepo {
    fun changeRequest(list: List<Article>): List<Article>

    //TOP_HEADLINES
    fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO>    //Не эффективно

    fun getTopicalHeadlinesCategoryCountry(category: String,country: String? = null): Single<ArticlesDTO>

    fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<ArticlesDTO> //Не эффективно

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

    //TOP_HEADLINES_SOURCES
    fun getSources(
        category: String? = null,
        language: String? = null,
        country: String? = null,
    ): Single<SourcesRequestDTO>

}