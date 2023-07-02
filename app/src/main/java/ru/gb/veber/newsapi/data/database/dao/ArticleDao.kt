package ru.gb.veber.newsapi.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.gb.veber.newsapi.data.database.entity.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertArticleV2(articleEntity: ArticleEntity)

    @Update
    suspend fun updateArticleV2(articleEntity: ArticleEntity)

    @Delete
    suspend fun deleteArticleV2(articleEntity: ArticleEntity)

    @Query("Delete from article where  is_favorites=0 and account_id =:accountId and date_added Like'%' || :dateAdded || '%'")
    suspend fun deleteArticleByIdHistoryGroupV2(accountId: Int, dateAdded: String)

    @Query("Delete from article where title =:title and is_favorites=1 and account_id =:accountId")
    suspend fun deleteArticleByIdFavoritesV2(title: String, accountId: Int)

    @Query("Delete from article where title =:title and is_favorites=0 and account_id =:accountId")
    suspend fun deleteArticleByIdHistoryV2(title: String, accountId: Int)

    @Query("Delete from article")
    suspend fun deleteAllArticleV2()

    @Query("Delete from article where account_id=:accountId and is_favorites=1")
    suspend fun deleteArticleIsFavoriteByIdV2(accountId: Int)

    @Query("Delete from article where account_id=:accountId and is_history=1 and is_favorites=0")
    suspend fun deleteArticleIsHistoryByIdV2(accountId: Int)

    @Query("Select * from article where account_id =:accountId and is_history=1 and is_favorites=0")
    suspend fun getHistoryArticleByIdV2(accountId: Int): List<ArticleEntity>

    @Query("Select * from article where account_id =:accountId and is_favorites=1")
    suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleEntity>

    @Query("Select * from article where account_id =:accountId")
    suspend fun getArticleByIdV2(accountId: Int): List<ArticleEntity>

    @Query("SELECT * FROM article ORDER BY id DESC  LIMIT 1")
    suspend fun getLastArticleV2(): ArticleEntity
}
