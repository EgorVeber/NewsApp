package ru.gb.veber.newsapi.data.repository

import ru.gb.veber.newsapi.data.database.dao.CountryDao
import ru.gb.veber.newsapi.data.mapper.toCountry
import ru.gb.veber.newsapi.data.mapper.toCountryEntity
import ru.gb.veber.newsapi.domain.models.CountryModel
import ru.gb.veber.newsapi.domain.repository.CountryRepo

class CountryRepoImpl(private val countryDao: CountryDao) : CountryRepo {
    override suspend fun insertAll(countryEntity: List<CountryModel>) {
        countryDao.insertAll(countryEntity.map { country -> country.toCountryEntity() })
    }

    override suspend fun getCountry(): List<CountryModel> {
        return countryDao.getCountry().map { countryEntity -> countryEntity.toCountry() }
    }
}
