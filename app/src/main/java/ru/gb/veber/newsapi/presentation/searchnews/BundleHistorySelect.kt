package ru.gb.veber.newsapi.presentation.searchnews

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BundleHistorySelect(
        private val key: String,
    ) : ReadWriteProperty<Fragment, HistorySelectModel> {

        private var cache: HistorySelectModel? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): HistorySelectModel {
            return cache ?: thisRef.arguments?.getParcelable<HistorySelectModel>(key).also {
                cache = it
            } ?: throw IllegalArgumentException()
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: HistorySelectModel) {
            (thisRef.arguments ?: Bundle().also { thisRef.arguments = it }).putParcelable(key, value)
            cache = value
        }
    }