package ru.gb.veber.newsapi.presentation.profile.account.settings.customize.helper

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}