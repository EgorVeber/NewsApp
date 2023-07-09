package ru.gb.veber.newsapi.presentation.base

import androidx.lifecycle.ViewModel

/** Базовый фрагмент наследоватся не обязательно*/
abstract class NewsViewModel : ViewModel() {
    abstract fun onBackPressedRouter():Boolean
}