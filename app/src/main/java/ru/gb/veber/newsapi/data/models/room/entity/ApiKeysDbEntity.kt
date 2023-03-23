package ru.gb.veber.newsapi.data.models.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "api_keys",
    foreignKeys = [ForeignKey(
        entity = AccountDbEntity::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class ApiKeysDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "account_id")
    val accountID: Int,
    @ColumnInfo(name = "key_api")
    var keyApi: String,
    @ColumnInfo(name = "actived")
    var actived: Int,
    @ColumnInfo(name = "first_request")
    var firstRequest: String,
    @ColumnInfo(name = "last_request")
    var lastRequest: String,
    @ColumnInfo(name = "count_request")
    var countRequest: Int,
    @ColumnInfo(name = "count_max")
    var countMax: Int,
)

