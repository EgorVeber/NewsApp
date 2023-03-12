package ru.gb.veber.newsapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gb.veber.newsapi.data.models.room.dao.*
import ru.gb.veber.newsapi.data.models.room.entity.*
import ru.gb.veber.newsapi.core.utils.DATABASE_NAME

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
            return Room.databaseBuilder(context, NewsDataBase::class.java, DATABASE_NAME).build()
        }
    }
}
