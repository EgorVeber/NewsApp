package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.gb.veber.newsapi.data.database.entity.CountryEntity

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countryEntity: List<CountryEntity>)

    @Query("Select * from country_directory")
    suspend fun getCountry(): List<CountryEntity>
}
