package ru.gb.veber.newsapi.data.models.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "country_directory",
)
data class CountryDbEntity(
    @PrimaryKey()
    val id: String,
    @ColumnInfo(name = "iso_three")
    val isoThree: String,
)
