package ru.gb.veber.newsapi.model.repository.room

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.model.database.dao.CountryDao
import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity
import ru.gb.veber.newsapi.utils.subscribeDefault

class CountryRepoImpl(private val countryDao: CountryDao) : CountryRepo {

    override fun insertAll(countryDbEntity: List<CountryDbEntity>): Completable {
        return countryDao.insertAll(countryDbEntity).subscribeDefault()
    }

    override fun getCountry(): Single<List<CountryDbEntity>> {
        return countryDao.getCountry().subscribeDefault()
    }
}