package ru.gb.veber.newsapi.data.repository


import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.Sources
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity

interface SourcesRepo {
    fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable
    fun getSources(): Single<MutableList<Sources>>

    suspend fun insertAllV2(sourcesDbEntity: List<SourcesDbEntity>)
    suspend fun getSourcesV2(): MutableList<Sources>
}
