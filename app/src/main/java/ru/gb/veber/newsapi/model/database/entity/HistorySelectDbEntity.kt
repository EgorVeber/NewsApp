package ru.gb.veber.newsapi.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "history_select",
    foreignKeys = [ForeignKey(
        entity = AccountDbEntity::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class HistorySelectDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "account_id")
    val accountID: Int,
    @ColumnInfo(name = "key_word")
    var keyWord: String,
    @ColumnInfo(name = "searchIn")
    var searchIn: String,
    @ColumnInfo(name = "sort_by_key_word")
    var sortByKeyWord: String,
    @ColumnInfo(name = "sort_by_sources")
    var sortBySources: String,
    @ColumnInfo(name = "sources_id")
    var sourcesId: String,
    @ColumnInfo(name = "date_sources")
    var dateSources: String,
    @ColumnInfo(name = "sources_name")
    var sourcesName: String,
)

