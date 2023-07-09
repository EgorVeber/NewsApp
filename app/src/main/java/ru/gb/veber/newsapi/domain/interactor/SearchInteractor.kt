package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.domain.models.AccountModel
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import ru.gb.veber.newsapi.domain.models.SourcesModel
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import ru.gb.veber.newsapi.domain.repository.AccountSourcesRepo
import ru.gb.veber.newsapi.domain.repository.HistorySelectRepo
import ru.gb.veber.newsapi.domain.repository.SourcesRepo
import ru.gb.veber.ui_common.ACCOUNT_ID_DEFAULT
import javax.inject.Inject

class SearchInteractor @Inject constructor(
    private val accountRepo: AccountRepo,
    private val historySelectRepo: HistorySelectRepo,
    private val sourcesRepo: SourcesRepo,
    private val accountSourcesRepo: AccountSourcesRepo,
) {
    suspend fun getAccountById(accountId: Int): AccountModel {
        return accountRepo.getAccountByIdV2(accountId)
    }

    suspend fun getSourcesAccount(accountId: Int, displayOnlySources: Boolean): List<SourcesModel> {
        val allSourcesList = sourcesRepo.getSources()

        if (accountId == ACCOUNT_ID_DEFAULT) return allSourcesList

        val likeSourcesList = getLikeSourcesFromAccount(accountId)

        if (displayOnlySources && likeSourcesList.isNotEmpty()) {
            return likeSourcesList
        }

        return changeSourcesListByLiked(likeSourcesList, allSourcesList.toMutableList())
    }

    suspend fun getHistoryById(accountId: Int): List<HistorySelectModel> {
        return historySelectRepo.getHistoryById(accountId).reversed()
    }

    suspend fun insertSelect(historySelectModel: HistorySelectModel) {
        historySelectRepo.insertSelect(historySelectModel)
    }

    suspend fun deleteSelectById(accountId: Int) {
        historySelectRepo.deleteSelectById(accountId)
    }

    suspend fun deleteSelect(historySelectModel: HistorySelectModel) {
        historySelectRepo.deleteSelect(historySelectModel)
    }

    private suspend fun getLikeSourcesFromAccount(accountId: Int): List<SourcesModel> {
        return accountSourcesRepo.getLikeSources(accountId)
    }

    private fun changeSourcesListByLiked(
        like: List<SourcesModel>,
        all: MutableList<SourcesModel>,
    ): MutableList<SourcesModel> {
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
