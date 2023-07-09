package ru.gb.veber.newsapi.di.moduls

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.gb.veber.newsapi.BuildConfig
import ru.gb.veber.newsapi.data.NewsService
import ru.gb.veber.ui_common.PAGE_SIZE
import ru.gb.veber.ui_common.PAGE_SIZE_COUNT
import javax.inject.Singleton

@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideNewsApi(client: OkHttpClient): NewsService =
        Retrofit.Builder().baseUrl(BuildConfig.NEWS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(NewsService::class.java)

    @Singleton
    @Provides
    fun client() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor { chain ->
        val request = chain.request()
        val url = request.url.newBuilder().addQueryParameter(PAGE_SIZE, PAGE_SIZE_COUNT).build()
        chain.proceed(request.newBuilder().url(url).build())
    }.build()

}
