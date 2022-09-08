package ru.gb.veber.newsapi.model.repository

import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.gb.veber.newsapi.model.Articles
import ru.gb.veber.newsapi.model.SourcesRequest
import ru.gb.veber.newsapi.utils.NEWS_BASE_URL

object NewsRetrofit {
     val newsTopSingle = Retrofit.Builder().baseUrl(NEWS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build().create(NewsApi::class.java)
}