package ru.gb.veber.newsapi.model.database.dao

import android.accounts.Account
import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity

@Dao
interface SourcesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(sourcesDbEntity: List<SourcesDbEntity>): Completable

    @Query("Select * from sources")
    fun getSources(): Single<List<SourcesDbEntity>>
}