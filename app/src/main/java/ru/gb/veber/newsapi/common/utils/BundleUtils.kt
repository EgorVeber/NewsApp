package ru.gb.veber.newsapi.common.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.gb.veber.newsapi.domain.models.HistorySelectModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BundleString(
    private val key: String,
    private val defaultValue: String,
) : ReadWriteProperty<Fragment, String> {

    private var cache: String? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): String {
        return cache ?: thisRef.arguments?.getString(key, defaultValue).also {
            cache = it
        } ?: throw IllegalArgumentException()
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: String) {
        (thisRef.arguments ?: Bundle().also { thisRef.arguments = it }).putString(key, value)
        cache = value
    }
}

class BundleInt(
    private val key: String,
    private val defaultValue: Int,
) : ReadWriteProperty<Fragment, Int> {

    private var cache: Int? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): Int {
        return cache ?: thisRef.arguments?.getInt(key, defaultValue).also {
            cache = it
        } ?: throw IllegalArgumentException()
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: Int) {
        (thisRef.arguments ?: Bundle().also { thisRef.arguments = it }).putInt(key, value)
        cache = value
    }
}

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
