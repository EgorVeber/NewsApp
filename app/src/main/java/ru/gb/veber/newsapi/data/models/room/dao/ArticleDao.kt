package ru.gb.veber.newsapi.data.models.room.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.gb.veber.newsapi.data.models.room.entity.ArticleDbEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertArticle(articleDbEntity: ArticleDbEntity): Completable

    @Update
    fun updateArticle(articleDbEntity: ArticleDbEntity): Completable

    @Delete
    fun deleteArticle(articleDbEntity: ArticleDbEntity): Completable

    @Query("Delete from article where  is_favorites=0 and account_id =:accountId and date_added Like'%' || :dateAdded || '%'")
    fun deleteArticleByIdHistoryGroup(accountId: Int, dateAdded: String): Completable

    @Query("Delete from article where title =:title and is_favorites=1 and account_id =:accountId")
    fun deleteArticleByIdFavorites(title: String, accountId: Int): Completable

    @Query("Delete from article where title =:title and is_favorites=0 and account_id =:accountId")
    fun deleteArticleByIdHistory(title: String, accountId: Int): Completable

    @Query("Delete from article")
    fun deleteAllArticle(): Completable

    @Query("Delete from article where account_id=:accountId and is_favorites=1")
    fun deleteArticleIsFavoriteById(accountId: Int): Completable

    @Query("Delete from article where account_id=:accountId and is_history=1 and is_favorites=0")
    fun deleteArticleIsHistoryById(accountId: Int): Completable

    @Query("Select * from article where account_id =:accountId and is_history=1 and is_favorites=0")
    fun getHistoryArticleById(accountId: Int): Single<List<ArticleDbEntity>>

    @Query("Select * from article where account_id =:accountId and is_favorites=1")
    fun getLikeArticleById(accountId: Int): Single<List<ArticleDbEntity>>

    @Query("Select * from article where account_id =:accountId")
    fun getArticleById(accountId: Int): Single<List<ArticleDbEntity>>

    @Query("SELECT * FROM article ORDER BY id DESC  LIMIT 1")
    fun getLastArticle(): Single<ArticleDbEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertArticleV2(articleDbEntity: ArticleDbEntity)

    @Update
    suspend fun updateArticleV2(articleDbEntity: ArticleDbEntity)

    @Delete
    suspend fun deleteArticleV2(articleDbEntity: ArticleDbEntity)

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
    suspend fun getHistoryArticleByIdV2(accountId: Int): List<ArticleDbEntity>

    @Query("Select * from article where account_id =:accountId and is_favorites=1")
    suspend fun getLikeArticleByIdV2(accountId: Int): List<ArticleDbEntity>

    @Query("Select * from article where account_id =:accountId")
    suspend fun getArticleByIdV2(accountId: Int): List<ArticleDbEntity>

    @Query("SELECT * FROM article ORDER BY id DESC  LIMIT 1")
    suspend fun getLastArticleV2(): ArticleDbEntity
}
