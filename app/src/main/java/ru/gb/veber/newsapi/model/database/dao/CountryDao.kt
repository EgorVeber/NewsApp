package ru.gb.veber.newsapi.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(countryDbEntity: List<CountryDbEntity>): Completable

    @Query("Select * from country_directory")
    fun getCountry(): Single<List<CountryDbEntity>>
}