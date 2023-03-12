package ru.gb.veber.newsapi.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistorySelect(
    val id: Int,
    var accountID: Int,
    var keyWord: String? = "",
    var searchIn: String? = "",
    var sortByKeyWord: String? = "",
    var sortBySources: String? = "",
    var sourcesId: String? = "",
    var dateSources: String? = "",
    var sourcesName: String? = "",
) : Parcelable