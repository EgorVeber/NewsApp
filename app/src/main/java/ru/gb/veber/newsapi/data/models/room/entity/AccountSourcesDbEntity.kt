package ru.gb.veber.newsapi.data.models.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "account_sources",
    primaryKeys = ["account_id", "sources_id"],
    foreignKeys = [
        ForeignKey(
            entity = AccountDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SourcesDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["sources_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)

data class AccountSourcesDbEntity(
    @ColumnInfo(name = "account_id") val accountId: Int,
    @ColumnInfo(name = "sources_id") val sourcesId: Int,
)
