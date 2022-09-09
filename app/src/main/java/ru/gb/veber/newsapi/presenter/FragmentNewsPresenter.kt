package ru.gb.veber.newsapi.presenter

import moxy.MvpPresenter

class FragmentNewsPresenter() :
    MvpPresenter<FragmentNewsView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
    }
}