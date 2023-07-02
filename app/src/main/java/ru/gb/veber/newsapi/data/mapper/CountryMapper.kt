package ru.gb.veber.newsapi.data.mapper

import ru.gb.veber.newsapi.data.database.entity.CountryEntity
import ru.gb.veber.newsapi.domain.models.CountryModel

fun CountryEntity.toCountry(): CountryModel = CountryModel(id = id, code = isoThree)

fun CountryModel.toCountryEntity(): CountryEntity = CountryEntity(id = id, isoThree = code)


