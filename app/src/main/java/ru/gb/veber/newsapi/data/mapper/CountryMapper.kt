package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.models.room.entity.CountryDbEntity
import ru.gb.veber.newsapi.domain.models.Country

fun CountryDbEntity.toCountry(): Country {
    return Country(this.id, this.isoThree)
}

