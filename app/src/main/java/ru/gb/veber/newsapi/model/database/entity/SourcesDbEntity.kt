package ru.gb.veber.newsapi.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

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
