package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.gb.veber.newsapi.data.database.entity.SourcesEntity

@Dao
interface SourcesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(sourcesEntity: List<SourcesEntity>)

    @Query("Select * from sources")
    suspend fun getSources(): List<SourcesEntity>
}
