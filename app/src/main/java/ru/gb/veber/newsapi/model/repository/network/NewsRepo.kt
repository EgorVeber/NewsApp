package ru.gb.veber.newsapi.model.repository.network

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.network.ArticlesDTO
import ru.gb.veber.newsapi.model.network.SourcesRequestDTO

interface NewsRepo {
    //TOP_HEADLINES
    fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): Single<ArticlesDTO>    //Не эффективно

    fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String? = null,
        key: String,
    ): Single<ArticlesDTO>

    fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
        key: String,
    ): Single<ArticlesDTO> //Не эффективно

    fun getTopicalHeadlinesSources(sources: String, key: String): Single<ArticlesDTO>

    //EVERYTHING
    fun getEverythingKeyWordSearchInSources(
        sources: String? = null,
        q: String? = null,
        searchIn: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): Single<ArticlesDTO>

    fun getEverythingKeyWordSearchIn(
        q: String,
        searchIn: String? = null,
        language: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): Single<ArticlesDTO>

    //TOP_HEADLINES_SOURCES
    fun getSources(
        category: String? = null,
        language: String? = null,
        country: String? = null,
        key: String,
    ): Single<SourcesRequestDTO>


    //TOP_HEADLINES
    suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): ArticlesDTO  //Не эффективно

    suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String? = null,
        key: String,
    ): ArticlesDTO

    suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        keyWord: String,
        sources: String,
        key: String,
    ): ArticlesDTO //Не эффективно

    suspend fun getTopicalHeadlinesSourcesV2(sources: String, key: String): ArticlesDTO

    //EVERYTHING
    suspend fun getEverythingKeyWordSearchInSourcesV2(
        sources: String? = null,
        q: String? = null,
        searchIn: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): ArticlesDTO

    suspend fun getEverythingKeyWordSearchInV2(
        q: String,
        searchIn: String? = null,
        language: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): ArticlesDTO

    //TOP_HEADLINES_SOURCES
    suspend fun getSourcesV2(
        category: String? = null,
        language: String? = null,
        country: String? = null,
        key: String,
    ): SourcesRequestDTO
}