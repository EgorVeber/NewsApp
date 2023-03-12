package ru.gb.veber.newsapi.data.models.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sources",
)
data class SourcesDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "id_sources")
    val idSources: String? = "",
    var name: String? = "",
    var description: String? = "",
    var url: String? = "",
    var category: String? = "",
    var language: String? = "",
    var country: String? = "",
)
