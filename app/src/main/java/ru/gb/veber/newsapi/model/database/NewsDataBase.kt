package ru.gb.veber.newsapi.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gb.veber.newsapi.model.database.dao.*
import ru.gb.veber.newsapi.model.database.entity.*

@Database(
    version = 1,
    entities = [
        AccountDbEntity::class,
        ArticleDbEntity::class,
        SourcesDbEntity::class,
        AccountSourcesDbEntity::class,
        HistorySelectDbEntity::class,
        CountryDbEntity::class]
)
abstract class NewsDataBase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun articleDao(): ArticleDao
    abstract fun sourcesDao(): SourcesDao
    abstract fun accountSourcesDao(): AccountSourcesDao
    abstract fun historySelectDao(): HistorySelectDao
    abstract fun countryDao(): CountryDao

    companion object {
        fun createDb(context: Context): NewsDataBase {
            return Room.databaseBuilder(context, NewsDataBase::class.java, "news.db").build()
        }
    }
}