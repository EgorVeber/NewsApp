package ru.gb.veber.newsapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gb.veber.newsapi.common.utils.DATABASE_NAME
import ru.gb.veber.newsapi.data.models.room.dao.AccountSourcesDao
import ru.gb.veber.newsapi.data.models.room.dao.AccountsDao
import ru.gb.veber.newsapi.data.models.room.dao.ArticleDao
import ru.gb.veber.newsapi.data.models.room.dao.CountryDao
import ru.gb.veber.newsapi.data.models.room.dao.HistorySelectDao
import ru.gb.veber.newsapi.data.models.room.dao.SourcesDao
import ru.gb.veber.newsapi.data.models.room.entity.AccountDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.HistorySelectDbEntity
import ru.gb.veber.newsapi.data.models.room.entity.SourcesDbEntity

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
