package ru.gb.veber.newsapi.model.repository.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity
import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity

interface CountryRepo {
    fun insertAll(countryDbEntity: List<CountryDbEntity>): Completable
    fun getCountry(): Single<List<CountryDbEntity>>
}