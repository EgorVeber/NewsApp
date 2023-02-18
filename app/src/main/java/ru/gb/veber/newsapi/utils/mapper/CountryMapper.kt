package ru.gb.veber.newsapi.utils.mapper

import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.model.database.entity.CountryDbEntity

fun CountryDbEntity.toCountry(): Country {
    return Country(this.id, this.isoThree)
}

