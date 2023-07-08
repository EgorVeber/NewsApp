package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.ArticlesBaseModel
import ru.gb.veber.newsapi.domain.models.SourcesBaseModel

interface NewsRepo {

    //TOP_HEADLINES
    suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): ArticlesBaseModel  //Не эффективно

    suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String? = null,
        key: String,
    ): ArticlesBaseModel

    suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        keyWord: String,
        sources: String,
        key: String,
    ): ArticlesBaseModel //Не эффективно

    suspend fun getTopicalHeadlinesSourcesV2(sources: String, key: String): ArticlesBaseModel

    //EVERYTHING
    suspend fun getEverythingKeyWordSearchInSourcesV2(
        sources: String? = null,
        q: String? = null,
        searchIn: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): ArticlesBaseModel

    suspend fun getEverythingKeyWordSearchInV2(
        q: String,
        searchIn: String? = null,
        language: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        key: String,
    ): ArticlesBaseModel

    //TOP_HEADLINES_SOURCES
    suspend fun getSourcesV2(
        category: String? = null,
        language: String? = null,
        country: String? = null,
        key: String,
    ): SourcesBaseModel
}
