package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity

fun newCountryDbEntity(key: String, value: String): CountryDbEntity {
    return CountryDbEntity(key, value)
}