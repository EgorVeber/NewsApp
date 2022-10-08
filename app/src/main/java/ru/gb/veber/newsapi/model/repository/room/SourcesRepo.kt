package ru.gb.veber.newsapi.model.repository.room


import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity

interface SourcesRepo {
    fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable
    fun getSources(): Single<MutableList<Sources>>
}