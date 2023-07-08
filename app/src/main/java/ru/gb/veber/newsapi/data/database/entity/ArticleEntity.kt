package ru.gb.veber.newsapi.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "article",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)

class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "account_id")
    val accountID: Int,
    var author: String,
    var description: String,
    @ColumnInfo(name = "published_at")
    var publishedAt: String,
    @ColumnInfo(name = "source_id")
    var sourceId: String,
    @ColumnInfo(name = "source_name")
    var sourceName: String,
    var title: String,
    var url: String,
    @ColumnInfo(name = "url_to_image")
    var urlToImage: String,
    @ColumnInfo(name = "is_history")
    var isHistory: Boolean,
    @ColumnInfo(name = "is_favorites")
    var isFavorites: Boolean,
    @ColumnInfo(name = "date_added")
    var dateAdded: String,
)

