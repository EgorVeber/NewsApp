package ru.gb.veber.newsapi.presentation.keymanagement

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.gb.veber.newsapi.domain.interactor.KeysManagementInteractor
import ru.gb.veber.newsapi.domain.models.ApiKeysModel
import ru.gb.veber.newsapi.presentation.base.NewsViewModel
import ru.gb.veber.ui_common.TAG_DB_ERROR
import ru.gb.veber.ui_common.coroutine.launchJob
import ru.gb.veber.ui_common.utils.DateFormatter.toStringFormatDateDayMonthYearHourMinutes
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
            error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
        })
    }

    fun addKey(accountId: Int, key: String) {
        viewModelScope.launchJob(tryBlock = {
            val apiKeysModel = ApiKeysModel(
                accountId = accountId,
                keyApi = key,
                firstRequest = Date().toStringFormatDateDayMonthYearHourMinutes(),
                lastRequest = Date().toStringFormatDateDayMonthYearHourMinutes())

            val keys = keysManagementInteractor.addKeys(apiKeysModel)
            keysManagementState.update { KeysManagementState.SetKeys(keys) }
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
        })
    }

    fun setNewKey(apiKeyModel: ApiKeysModel) {
        viewModelScope.launchJob(tryBlock = {
            val keys = keysManagementInteractor.setNewKey(apiKeyModel)
            keysManagementState.update { KeysManagementState.SetKeys(keys) }
        }, catchBlock = { error ->
            error.localizedMessage?.let { Log.d(TAG_DB_ERROR, it) }
        })
    }

    sealed class KeysManagementState {
        data class SetKeys(val keys: List<ApiKeysModel>) : KeysManagementState()
        object EmptyList : KeysManagementState()
        object StartedState : KeysManagementState()
    }
}