package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity

fun mapToCountry(key: String, value: String): CountryDbEntity {
    return CountryDbEntity(key, value)
}