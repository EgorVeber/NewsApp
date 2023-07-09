package ru.gb.veber.newsapi.domain.interactor

import ru.gb.veber.newsapi.common.PrefsAccountHelper
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import javax.inject.Inject

class CustomizeCategoryInteractor @Inject constructor(
    private val sharedPreferenceAccount: PrefsAccountHelper,
    private val accountRepo: AccountRepo
) {

    fun getArray(): Array<String> {
        return sharedPreferenceAccount.getArrayCategories()
    }
}