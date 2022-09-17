package ru.gb.veber.newsapi.model.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.gb.veber.newsapi.BuildConfig
import ru.gb.veber.newsapi.utils.API_KEY
import ru.gb.veber.newsapi.utils.PAGE_SIZE
import ru.gb.veber.newsapi.utils.PAGE_SIZE_COUNT

object NewsRetrofit {
    val newsTopSingle: NewsApi =
        Retrofit.Builder().baseUrl(BuildConfig.NEWS_BASE_URL)
            .client(client())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(NewsApi::class.java)


    private fun client() = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request()
        val url = request.url.newBuilder().addQueryParameter(API_KEY, BuildConfig.KEY_NEWS)
            .addQueryParameter(PAGE_SIZE,
                PAGE_SIZE_COUNT).build()
        chain.proceed(request.newBuilder().url(url).build())
    }.build()
}