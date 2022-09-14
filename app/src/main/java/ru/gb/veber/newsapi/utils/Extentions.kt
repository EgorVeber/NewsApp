package ru.gb.veber.newsapi.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.gb.veber.newsapi.R

fun <T> Single<T>.subscribeDefault(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun ImageView.loadGlide(url: String?) {
    Glide.with(context).load(url).into(this)
}
