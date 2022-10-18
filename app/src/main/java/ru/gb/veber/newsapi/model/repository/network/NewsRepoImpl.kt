package ru.gb.veber.newsapi.model.repository.network

import android.annotation.SuppressLint
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.core.App
import ru.gb.veber.newsapi.model.Article
import ru.gb.veber.newsapi.model.ArticlesDTO
import ru.gb.veber.newsapi.model.SourcesRequestDTO
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.utils.*
import java.text.SimpleDateFormat
import java.util.*

class NewsRepoImpl(private val newsApi: NewsApi) : NewsRepo {

    @SuppressLint("SimpleDateFormat")
    override fun changeRequest(list: List<Article>): List<Article> {
        return list.map {

            val publishedDate = stringFromData(it.publishedAt)
            val simpleFormat = SimpleDateFormat(FORMAT_DATE)

            val todayStr = App.instance.applicationContext.getString(R.string.today)
            val yesterdayStr = App.instance.applicationContext.getString(R.string.yesterday)
            val readOnStr = App.instance.applicationContext.getString(R.string.readOn)
            val anonymousOnStr = App.instance.applicationContext.getString(R.string.anonymousAuthor)

            if (simpleFormat.format(publishedDate).equals(simpleFormat.format(Date()))) {
                it.publishedAtChange = "$todayStr ${publishedDate.formatHour()}"
            } else if (simpleFormat.format(publishedDate)
                    .equals(simpleFormat.format(takeDate(-1)))
            ) {
                it.publishedAtChange = "$yesterdayStr ${publishedDate.formatHour()}"
            } else {
                it.publishedAtChange = publishedDate.formatDateTime()
            }

            if (it.title == null || it.description.equals("")) {
                it.title = it.publishedAt
            }
            if (it.source.id == null) {
                it.source.id = ""
            }

            if (it.description.equals("") || it.description == null) {
                it.description = " $readOnStr ${it.source.name}"

            } else {
                it.description += "."
            }

            if (it.author == null || it.author.equals("") || it.author.equals(" ")) {
                it.author = anonymousOnStr
            }
            it
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