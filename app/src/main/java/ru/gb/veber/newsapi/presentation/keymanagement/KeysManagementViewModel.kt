package ru.gb.veber.newsapi.presentation.keymanagement

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.common.extentions.DateFormatter.formatKeys
import ru.gb.veber.newsapi.common.extentions.launchJob
import ru.gb.veber.newsapi.common.utils.ERROR_DB
import ru.gb.veber.newsapi.domain.interactor.KeysManagementInteractor
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import java.util.Date
import javax.inject.Inject

class KeysManagementViewModel @Inject constructor(
    private val router: Router,
    private val keysManagementInteractor: KeysManagementInteractor,
) : NewsViewModel() {

    private val keysManagementState: MutableStateFlow<KeysManagementState> =
        MutableStateFlow(KeysManagementState.StartedState)
    val keysManagementFlow = keysManagementState.asStateFlow()


    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun back() {
        router.exit()
    }

    fun getKeys(accountId: Int) {
        viewModelScope.launchJob(tryBlock = {
            val keys: List<ApiKeysModel> = keysManagementInteractor.getKeys(accountId)
            if (keys.isNotEmpty()) {
                keysManagementState.update { KeysManagementState.SetKeys(keys) }
            } else {
                keysManagementState.update { KeysManagementState.EmptyList }
            }

        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(ERROR_DB, it) }
        })
    }

    fun addKey(accountId: Int, key: String) {
        viewModelScope.launchJob(tryBlock = {
            val apiKeysModel = ApiKeysModel(
                accountId = accountId,
                keyApi = key,
                firstRequest = Date().formatKeys(),
                lastRequest = Date().formatKeys())

            val keys = keysManagementInteractor.addKeys(apiKeysModel)
            keysManagementState.update { KeysManagementState.SetKeys(keys) }
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(ERROR_DB, it) }
        })
    }

    fun setNewKey(apiKeyModel: ApiKeysModel) {
        viewModelScope.launchJob(tryBlock = {
            val keys = keysManagementInteractor.setNewKey(apiKeyModel)
            keysManagementState.update { KeysManagementState.SetKeys(keys) }
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(ERROR_DB, it) }
        })
    }

    sealed class KeysManagementState {
        data class SetKeys(val keys: List<ApiKeysModel>) : KeysManagementState()
        object EmptyList : KeysManagementState()
        object StartedState : KeysManagementState()
    }
}