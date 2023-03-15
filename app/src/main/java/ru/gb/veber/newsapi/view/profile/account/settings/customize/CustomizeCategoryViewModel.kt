package ru.gb.veber.newsapi.view.profile.account.settings.customize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.data.SharedPreferenceAccount
import ru.gb.veber.newsapi.domain.repository.AccountRepo
import javax.inject.Inject

class CustomizeCategoryViewModel @Inject constructor(
    private val router: Router,
    private val sharedPreferenceAccount: SharedPreferenceAccount,
    private val accountRepo: AccountRepo
) : ViewModel() {

    private val customList: MutableLiveData<MutableList<Category>> = MutableLiveData()
    private val _customList: LiveData<MutableList<Category>> = customList

    init {
        val list: MutableList<Category> = mutableListOf()
        val listFromArray = sharedPreferenceAccount.getArrayCategories()

        listFromArray.forEach {
            list.add(Category(it))
        }
        customList.value = list
    }

    fun getLiveDataCategory() : LiveData<MutableList<Category>> {
        return _customList
    }

    fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    fun backAccountScreen() {
        router.exit()
    }

    sealed class CustomizeCategoryState {
        data class ListChanged(val list: MutableList<Category>): CustomizeCategoryState()
    }
}