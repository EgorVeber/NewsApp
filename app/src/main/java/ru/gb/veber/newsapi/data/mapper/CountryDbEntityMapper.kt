package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity

fun newCountryDbEntity(key: String, value: String): CountryDbEntity {
    return CountryDbEntity(key, value)
}
