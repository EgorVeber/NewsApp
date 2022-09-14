package ru.gb.veber.newsapi.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Single<T>.subscribeDefault():Single<T> {
  return  this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}