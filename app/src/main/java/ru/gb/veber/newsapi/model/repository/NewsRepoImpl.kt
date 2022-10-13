package ru.gb.veber.newsapi.model.repository

import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticlesDTO
import ru.gb.veber.newsapi.model.SourcesRequestDTO
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.utils.*
import java.text.SimpleDateFormat
import java.util.*

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {

    override fun changeRequest(list: List<Article>): List<Article> {
        return list.map {
            var publishedDate = stringFromData(it.publishedAt)
            var SDF = SimpleDateFormat("yyyy-MM-dd")

            if (SDF.format(publishedDate).equals(SDF.format(Date()))) {
                it.publishedAtChange = "Today ${publishedDate.formatHour()}"
            } else if (SDF.format(publishedDate).equals(SDF.format(takeDate(-1)))) {
                it.publishedAtChange = "Yesterday ${publishedDate.formatHour()}"
            } else {
                it.publishedAtChange = publishedDate.formatDateTime()
            }

            if (it.source.id == null) {
                it.source.id = "Не проверенный источник"
            }

            if (it.description.equals("") || it.description == null) {
                it.description = " Read on ${it.source.name}"
            } else {
                it.description += "."
            }

            if (it.author == null || it.author.equals("") || it.author.equals(" ")) {
                it.author = "Anonymous source"
            }
            it
        }.also {
            it[0].viewType = 1
        }
    }

    //TOP_HEADLINES
    override fun getTopicalHeadlinesCountryCategoryKeyword(
        country: String,
        category: String,
        keyWord: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCountryCategoryKeyword(country, category, keyWord)
            .subscribeDefault()

    override fun getTopicalHeadlinesCategoryCountry(
        category: String,
        country: String?,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesCategoryCountry(category, country).subscribeDefault()


    override fun getTopicalHeadlinesSourcesKeyWord(
        keyWord: String,
        sources: String,
    ): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSourcesKeyWord(keyWord, sources).subscribeDefault()


    override fun getTopicalHeadlinesSources(sources: String): Single<ArticlesDTO> =
        newsApi.getTopicalHeadlinesSources(sources).subscribeDefault()


    //Everything
    override fun getEverythingKeyWordSearchIn(
        q: String,
        searchIn: String?,
        language: String?,
        sortBy: String?,
        from: String?,
        to: String?,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchIn(q, searchIn, language, sortBy, from, to)
            .subscribeDefault()


    override fun getEverythingKeyWordSearchInSources(
        sources: String?,
        q: String?,
        searchIn: String?,
        sortBy: String?,
        from: String?,
        to: String?,
    ): Single<ArticlesDTO> =
        newsApi.getEverythingKeyWordSearchInSources(sources, q, searchIn, sortBy, from, to)
            .subscribeDefault()

    //TOP_HEADLINES_SOURCES
    override fun getSources(
        category: String?,
        language: String?,
        country: String?,
    ): Single<SourcesRequestDTO> =
        newsApi.getSources(category, language, country).subscribeDefault()
}