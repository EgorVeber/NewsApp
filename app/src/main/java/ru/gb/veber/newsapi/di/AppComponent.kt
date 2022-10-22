package ru.gb.veber.newsapi.di

import dagger.Component
import ru.gb.veber.newsapi.presenter.ActivityPresenter
import ru.gb.veber.newsapi.view.activity.ActivityMain
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NavigationModule::class, AppModule::class]
)
interface AppComponent {
    fun inject(mainActivity: ActivityMain)
    fun inject(mainPresenter: ActivityPresenter)
}