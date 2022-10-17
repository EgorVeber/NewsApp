package ru.gb.veber.newsapi.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index("email", unique = true), Index("userName", unique = true)]
)
data class AccountDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val userName: String,
    val password: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val email: String,
    val createdAt: String,
    @ColumnInfo(name = "save_history")
    val saveHistory: Boolean,
    @ColumnInfo(name = "save_select_history")
    var saveSelectHistory: Boolean = true,
    @ColumnInfo(name = "display_only_sources")
    var displayOnlySources: Boolean = false,
    @ColumnInfo(name = "my_country")
    var myCountry: String,
)