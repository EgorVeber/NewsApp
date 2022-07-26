package ru.gb.veber.newsapi.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.gb.veber.newsapi.BuildConfig
import ru.gb.veber.newsapi.model.network.NewsApi
import ru.gb.veber.newsapi.utils.API_KEY
import ru.gb.veber.newsapi.utils.PAGE_SIZE
import ru.gb.veber.newsapi.utils.PAGE_SIZE_COUNT
import javax.inject.Singleton

@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideNewsApi(client: OkHttpClient): NewsApi =
        Retrofit.Builder().baseUrl(BuildConfig.NEWS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(NewsApi::class.java)

    @Singleton
    @Provides
    fun client() = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor { chain ->
        val request = chain.request()
        val url = request.url.newBuilder().addQueryParameter(API_KEY, BuildConfig.KEY_NEWS).addQueryParameter(PAGE_SIZE, PAGE_SIZE_COUNT).build()
        chain.proceed(request.newBuilder().url(url).build())
    }.build()
}