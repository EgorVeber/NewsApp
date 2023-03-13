package ru.gb.veber.newsapi.view.profile.account.settings.customize.helper

import ru.gb.veber.newsapi.view.profile.account.settings.customize.Category
import ru.gb.veber.newsapi.view.profile.account.settings.customize.CustomizeCategoryAdapter

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

interface EventDraw {
    fun onStartDrag(holder: CustomizeCategoryAdapter.ItemViewHolder)
    fun setNewList(dataList: MutableList<Category>)
}