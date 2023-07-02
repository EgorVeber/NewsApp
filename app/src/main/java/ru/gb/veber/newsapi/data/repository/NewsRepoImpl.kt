package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.NewsService
import ru.gb.veber.newsapi.data.mapper.toArticleBaseModel
import ru.gb.veber.newsapi.data.mapper.toSourcesBaseModel
import ru.gb.veber.newsapi.domain.models.ArticlesBaseModel
import ru.gb.veber.newsapi.domain.models.SourcesBaseModel
import ru.gb.veber.newsapi.domain.repository.NewsRepo

class NewsRepoImpl(private val newsService: NewsService) : NewsRepo {

    override suspend fun getTopicalHeadlinesCountryCategoryKeywordV2(
        country: String,
        category: String,
        keyWord: String,
        key: String,
    ): ArticlesBaseModel =
        newsService.getTopicalHeadlinesCountryCategoryKeywordV2(country, category, keyWord, key)
            .toArticleBaseModel()

    override suspend fun getTopicalHeadlinesCategoryCountryV2(
        category: String,
        country: String?,
        key: String,
    ): ArticlesBaseModel =
        newsService.getTopicalHeadlinesCategoryCountryV2(category, country, key).toArticleBaseModel()


    override suspend fun getTopicalHeadlinesSourcesKeyWordV2(
        keyWord: String,
        sources: String,
        key: String,
    ): ArticlesBaseModel =
        newsService.getTopicalHeadlinesSourcesKeyWordV2(keyWord, sources, key).toArticleBaseModel()


    override suspend fun getTopicalHeadlinesSourcesV2(
        sources: String,
        key: String,
    ): ArticlesBaseModel =
        newsService.getTopicalHeadlinesSourcesV2(sources, key).toArticleBaseModel()

    override suspend fun getEverythingKeyWordSearchInV2(
        q: String,
        searchIn: String?,
        language: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): ArticlesBaseModel =
        newsService.getEverythingKeyWordSearchInV2(q, searchIn, language, sortBy, from, to, key)
            .toArticleBaseModel()


    override suspend fun getEverythingKeyWordSearchInSourcesV2(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        key: String,
    ): ArticlesBaseModel =
        newsService.getEverythingKeyWordSearchInSourcesV2(sources, q, searchIn, sortBy, from, to, key)
            .toArticleBaseModel()

    override suspend fun getSourcesV2(
        category: String?,
        language: String?,
        country: String?,
        key: String,
    ): SourcesBaseModel =
        newsService.getSourcesV2(category, language, country, key).toSourcesBaseModel()
}
