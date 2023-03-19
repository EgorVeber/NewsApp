package ru.gb.veber.newsapi.presentation.profile.account.settings.customize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.common.base.NewsViewModel
import ru.gb.veber.newsapi.domain.interactor.CustomizeCategoryInteractor
import javax.inject.Inject

class CustomizeCategoryViewModel @Inject constructor(
    private val router: Router,
    private val customizeCategoryInteractor: CustomizeCategoryInteractor
) : NewsViewModel() {

    private val customList: MutableLiveData<MutableList<Category>> = MutableLiveData()
    private val _customList: LiveData<MutableList<Category>> = customList

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return true
    }

    init {
        val list: MutableList<Category> = mutableListOf()
        val listFromArray = customizeCategoryInteractor.getArray()

        listFromArray.forEach {
            list.add(Category(it))
        }
        customList.value = list
    }

    fun getLiveDataCategory() : LiveData<MutableList<Category>> {
        return _customList
    }

    fun backAccountScreen() {
        router.exit()
    }

    sealed class CustomizeCategoryState {
        data class ListChanged(val list: MutableList<Category>): CustomizeCategoryState()
    }
}