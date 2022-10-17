package ru.gb.veber.newsapi.utils

import ru.gb.veber.newsapi.model.*
import ru.gb.veber.newsapi.model.database.entity.*


fun mapToArticleDTO(item: ArticlesDTO): List<Article> {
    return item.articles.map(::mapToArticle)
}

fun mapToArticle(item: ArticleDTO): Article {
    return Article(
        author = item.author,
        description = item.description,
        publishedAt = item.publishedAt,
        publishedAtChange = item.publishedAt,
        source = mapToSources(item.source),
        title = item.title,
        url = item.url,
        urlToImage = item.urlToImage,
    )
}


fun mapToSourcesDbEntity(sourcesId: String, sourcesName: String): Source {
    return Source(
        id = sourcesId,
        name = sourcesName
    )
}

fun mapToSources(item: SourceDTO): Source {
    return Source(
        id = item.id,
        name = item.name
    )
}

//ROOM
fun mapToAccount(item: AccountDbEntity): Account {
    return Account(
        id = item.id,
        userName = item.userName,
        email = item.email,
        createdAt = item.createdAt,
        password = item.password,
        saveHistory = item.saveHistory,
        displayOnlySources = item.displayOnlySources,
        saveSelectHistory = item.saveSelectHistory
    )
}

fun mapToAccountDbEntity(item: Account): AccountDbEntity {
    return AccountDbEntity(
        id = item.id,
        userName = item.userName,
        email = item.email,
        createdAt = item.createdAt,
        password = item.password,
        saveHistory = item.saveHistory,
        saveSelectHistory = item.saveSelectHistory,
        displayOnlySources = item.displayOnlySources,
        myCountry = item.myCountry
    )
}


fun mapToHistorySelectDbEntity(item: HistorySelect): HistorySelectDbEntity {
    return HistorySelectDbEntity(
        id = item.id,
        accountID = item.accountID,
        keyWord = item.keyWord.toString(),
        searchIn = item.searchIn.toString(),
        sortByKeyWord = item.sortByKeyWord.toString(),
        sortBySources = item.sortBySources.toString(),
        sourcesId = item.sourcesId.toString(),
        dateSources = item.dateSources.toString(),
        sourcesName = item.sourcesName.toString()
    )
}

fun mapToHistorySelect(item: HistorySelectDbEntity): HistorySelect {
    return HistorySelect(
        id = item.id,
        accountID = item.accountID,
        keyWord = item.keyWord,
        searchIn = item.searchIn,
        sortByKeyWord = item.sortByKeyWord,
        sortBySources = item.sortBySources,
        sourcesId = item.sourcesId,
        dateSources = item.dateSources,
        sourcesName = item.sourcesName
    )
}


fun mapToArticleDbEntity(article: Article, accountId: Int): ArticleDbEntity {
    return ArticleDbEntity(
        id = 0,
        accountID = accountId,
        author = article.author,
        description = article.description,
        publishedAt = article.publishedAt,
        sourceId = article.source.id ?: "none",
        sourceName = article.source.name,
        title = article.title.toString(),
        url = article.url,
        urlToImage = article.urlToImage,
        isHistory = article.isHistory,
        isFavorites = article.isFavorites,
        dateAdded = article.dateAdded.toString()
    )
}


fun articleDbEntityToArticle(item: ArticleDbEntity): Article {
    return Article(
        author = item.author,
        description = item.description,
        publishedAt = item.publishedAt,
        publishedAtChange = item.publishedAt,
        source = mapToSourcesDbEntity(item.sourceId, item.sourceName),
        title = item.title,
        url = item.url,
        urlToImage = item.urlToImage,
    )
}


fun sourcesDtoToEntity(sourcesDTO: SourcesDTO): SourcesDbEntity {
    return SourcesDbEntity(
        id = 0,
        idSources = sourcesDTO.id,
        name = sourcesDTO.name,
        description = sourcesDTO.description,
        url = sourcesDTO.url,
        category = sourcesDTO.category,
        language = sourcesDTO.language,
        country = sourcesDTO.country
    )
}

fun sourcesDbEntityToSources(sourcesDb: SourcesDbEntity): Sources {
    return Sources(
        id = sourcesDb.id,
        idSources = sourcesDb.idSources,
        name = sourcesDb.name,
        description = sourcesDb.description,
        url = sourcesDb.url,
        category = sourcesDb.category,
        language = sourcesDb.language,
        country = sourcesDb.country
    )

}

fun sourcesToDbEntity(sources: Sources): SourcesDbEntity {
    return SourcesDbEntity(
        id = sources.id,
        idSources = sources.idSources,
        name = sources.name,
        description = sources.description,
        url = sources.url,
        category = sources.category,
        language = sources.language,
        country = sources.country
    )
}


fun mapToCountry(key: String, value: String): CountryDbEntity {
    return CountryDbEntity(key, value)
}

fun mapToDbEntityCountry(countryDbEntity: CountryDbEntity): Country {
    return Country(countryDbEntity.id, countryDbEntity.isoThree)
}


