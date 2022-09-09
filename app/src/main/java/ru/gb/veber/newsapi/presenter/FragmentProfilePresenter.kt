package ru.gb.veber.newsapi.presenter

import moxy.MvpPresenter

class FragmentProfilePresenter() :
    MvpPresenter<FragmentProfileView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }
}