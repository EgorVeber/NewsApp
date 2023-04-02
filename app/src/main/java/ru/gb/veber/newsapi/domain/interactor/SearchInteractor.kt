package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.data.mapper.toHistorySelect
import ru.gb.veber.newsapi.data.mapper.toHistorySelectDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.domain.models.Account
import ru.gb.veber.newsapi.domain.models.HistorySelect
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
    suspend fun getAccountById(accountId: Int): Account {
        return accountRepo.getAccountByIdV2(accountId)
    }

    suspend fun getSourcesAccount(accountId: Int, displayOnlySources: Boolean): List<Sources> {
        val allSourcesList = sourcesRepo.getSourcesV2()

        if (accountId == ACCOUNT_ID_DEFAULT) return allSourcesList

        val likeSourcesList = getLikeSourcesFromAccount(accountId)

        if (displayOnlySources && likeSourcesList.isNotEmpty()) {
            return likeSourcesList
        }

        return changeSourcesListByLiked(likeSourcesList, allSourcesList)
    }

    suspend fun getHistoryById(accountId: Int): List<HistorySelect> {
        return historySelectRepo.getHistoryByIdV2(accountId).map { it.toHistorySelect() }.reversed()
    }

    suspend fun insertSelect(historySelect: HistorySelect) {
        historySelectRepo.insertSelectV2(historySelect.toHistorySelectDbEntity())
    }

    suspend fun deleteSelectById(accountId: Int) {
        historySelectRepo.deleteSelectByIdV2(accountId)
    }

    suspend fun deleteSelect(historySelect: HistorySelect) {
        historySelectRepo.deleteSelectV2(historySelect.toHistorySelectDbEntity())
    }

    private suspend fun getLikeSourcesFromAccount(accountId: Int): List<Sources> {
        return accountSourcesRepo.getLikeSourcesFromAccountV2(accountId)
    }

    private fun changeSourcesListByLiked(
        like: List<Sources>,
        all: MutableList<Sources>,
    ): MutableList<Sources> {
        for (j in like.size - 1 downTo 0) {
            for (i in all.indices) {
                if (like[j].idSources == all[i].idSources) {
                    all.removeAt(i)
                    all.add(0, like[j])
                }
            }
        }
        return all
    }
}
