package ru.gb.veber.newsapi.domain.repository

import ru.gb.veber.newsapi.domain.models.CountryModel

interface CountryRepo {
    suspend fun insertAll(countryEntity: List<CountryModel>)
    suspend fun getCountry(): List<CountryModel>
}
