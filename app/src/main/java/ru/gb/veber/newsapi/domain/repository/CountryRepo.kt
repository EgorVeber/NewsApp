package ru.gb.veber.newsapi.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity

interface CountryRepo {
    fun insertAll(countryDbEntity: List<CountryDbEntity>): Completable
    fun getCountry(): Single<List<CountryDbEntity>>
    suspend fun insertAllV2(countryDbEntity: List<CountryDbEntity>)
    suspend fun getCountryV2(): List<CountryDbEntity>
}