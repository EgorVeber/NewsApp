package ru.gb.veber.newsapi.presentation.keymanagement.recycler

import ru.gb.veber.newsapi.domain.models.ApiKeysModel

interface KeysListener {
    fun checkBoxClick(apiKeyModel: ApiKeysModel)
    fun holderClick()
}