package ru.gb.veber.newsapi.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gb.veber.newsapi.model.database.dao.AccountSourcesDao
import ru.gb.veber.newsapi.model.database.dao.AccountsDao
import ru.gb.veber.newsapi.model.database.dao.ArticleDao
import ru.gb.veber.newsapi.model.database.dao.SourcesDao
import ru.gb.veber.newsapi.model.database.entity.AccountDbEntity
import ru.gb.veber.newsapi.model.database.entity.AccountSourcesDbEntity
import ru.gb.veber.newsapi.model.database.entity.ArticleDbEntity
import ru.gb.veber.newsapi.model.database.entity.SourcesDbEntity

@Database(
    version = 1,
    entities = [AccountDbEntity::class, ArticleDbEntity::class, SourcesDbEntity::class, AccountSourcesDbEntity::class]
)
abstract class NewsDataBase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun articleDao(): ArticleDao
    abstract fun sourcesDao(): SourcesDao
    abstract fun accountSourcesDao(): AccountSourcesDao

    companion object {
        fun createDb(context: Context): NewsDataBase {
            return Room.databaseBuilder(context, NewsDataBase::class.java, "news.db").build()
        }
    }
}