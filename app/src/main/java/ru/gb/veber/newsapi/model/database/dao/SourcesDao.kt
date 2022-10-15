package ru.gb.veber.newsapi.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity

@Dao
interface SourcesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable

    @Query("Select * from sources")
    fun getSources(): Single<List<SourcesDbEntity>>

}