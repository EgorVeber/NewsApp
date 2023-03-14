package ru.gb.veber.newsapi.domain.interactor

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.Sources
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import javax.inject.Inject

class SearchInteractor @Inject constructor(
    private val accountRepo: AccountRepo,
    private val historySelectRepo: HistorySelectRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
) {
    fun getAccountById(accountId: Int): Single<Account> {
        return accountRepo.getAccountById(accountId)
    }

    fun getLikeSourcesFromAccount(accountId: Int): Single<List<Sources>> {
        return accountSourcesRepo.getLikeSourcesFromAccount(accountId)
    }

    fun getSources(): Single<MutableList<Sources>> {
        return sourcesRepo.getSources()
    }

    fun insertSelect(toHistorySelectDbEntity: HistorySelectDbEntity): Completable {
        return historySelectRepo.insertSelect(toHistorySelectDbEntity)
    }

    fun deleteSelectById(accountId: Int): Completable {
        return historySelectRepo.deleteSelectById(accountId)
    }

    fun deleteSelect(toHistorySelectDbEntity: HistorySelectDbEntity): Completable {
        return historySelectRepo.deleteSelect(toHistorySelectDbEntity)
    }

    fun getHistoryById(accountId: Int): Single<List<HistorySelectDbEntity>> {
        return historySelectRepo.getHistoryById(accountId)
    }
}
