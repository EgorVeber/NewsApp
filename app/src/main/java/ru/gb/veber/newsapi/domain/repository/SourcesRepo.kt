package ru.gb.veber.newsapi.domain.repository


import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity
import ru.gb.veber.newsapi.domain.models.Sources

interface SourcesRepo {
    fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable
    fun getSources(): Single<MutableList<Sources>>

    suspend fun insertAllV2(sourcesDbEntity: List<SourcesDbEntity>)
    suspend fun getSourcesV2(): MutableList<Sources>
}
