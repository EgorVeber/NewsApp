package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.model.Country
import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity

fun CountryDbEntity.toCountry(): Country {
    return Country(this.id, this.isoThree)
}

