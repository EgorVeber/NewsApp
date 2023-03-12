package ru.gb.veber.newsapi.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.common.extentions.subscribeDefault
import ru.gb.veber.newsapi.data.models.room.dao.CountryDao
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.domain.repository.CountryRepo

class CountryRepoImpl(private val countryDao: CountryDao) : CountryRepo {

    override fun insertAll(countryDbEntity: List<CountryDbEntity>): Completable {
        return countryDao.insertAll(countryDbEntity).subscribeDefault()
    }

    override fun getCountry(): Single<List<CountryDbEntity>> {
        return countryDao.getCountry().subscribeDefault()
    }

    override suspend fun insertAllV2(countryDbEntity: List<CountryDbEntity>) {
        countryDao.insertAllV2(countryDbEntity)
    }

    override suspend fun getCountryV2(): List<CountryDbEntity> {
        return countryDao.getCountryV2()
    }
}
