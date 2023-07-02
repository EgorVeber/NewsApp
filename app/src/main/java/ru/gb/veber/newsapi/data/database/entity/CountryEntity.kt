package ru.gb.veber.newsapi.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "country_directory",
)
class CountryEntity(
    @PrimaryKey()
    val id: String,
    @ColumnInfo(name = "iso_three")
    val isoThree: String,
)
