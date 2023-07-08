package ru.gb.veber.newsapi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gb.veber.newsapi.common.utils.DATABASE_NAME
import ru.gb.veber.newsapi.data.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.data.database.dao.AccountsDao
import ru.gb.veber.newsapi.data.database.dao.ApiKeysDao
import ru.gb.veber.newsapi.data.database.dao.ArticleDao
import ru.gb.veber.newsapi.data.database.dao.CountryDao
import ru.gb.veber.newsapi.data.database.dao.HistorySelectDao
import ru.gb.veber.newsapi.data.database.dao.SourcesDao
import ru.gb.veber.newsapi.data.database.entity.AccountEntity
import ru.gb.veber.newsapi.data.database.entity.AccountSourcesEntity
import ru.gb.veber.newsapi.data.database.entity.ApiKeysEntity
import ru.gb.veber.newsapi.data.database.entity.ArticleEntity
import ru.gb.veber.newsapi.data.database.entity.CountryEntity
import ru.gb.veber.newsapi.data.database.entity.HistorySelectEntity
import ru.gb.veber.newsapi.data.database.entity.SourcesEntity

@Database(
    version = 1,
    entities = [
        AccountEntity::class,
        ArticleEntity::class,
        SourcesEntity::class,
        AccountSourcesEntity::class,
        HistorySelectEntity::class,
        CountryEntity::class,
        ApiKeysEntity::class]
)
abstract class NewsDataBase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun articleDao(): ArticleDao
    abstract fun sourcesDao(): SourcesDao
    abstract fun accountSourcesDao(): AccountSourcesDao
    abstract fun historySelectDao(): HistorySelectDao
    abstract fun countryDao(): CountryDao
    abstract fun apiKeysDao(): ApiKeysDao

    companion object {
        fun createDb(context: Context): NewsDataBase {
            return Room.databaseBuilder(context, NewsDataBase::class.java, DATABASE_NAME).build()
        }
    }
}
