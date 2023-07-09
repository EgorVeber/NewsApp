package ru.gb.veber.ui_core.view

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import ru.gb.veber.ui_core.R

@SuppressLint("ResourceType")
class NewsSnackBar(parent: ViewGroup, content: SnackBarView) :
    BaseTransientBottomBar<NewsSnackBar>(parent, content, content) {

    init {
        val tv = TypedValue()
        view.context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
        val actionBarHeight: Int = view.context.resources.getDimensionPixelSize(tv.resourceId)

        getView().setBackgroundResource(ContextCompat.getColor(view.context,
            android.R.color.transparent))
        getView().setPadding(0, 0, 0, actionBarHeight)
    }

    companion object {
        fun make(viewGroup: ViewGroup, title: String, length: Int?): NewsSnackBar {
            val customView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.snackbar_layout, viewGroup, false) as SnackBarView
            customView.setText(title)
            return NewsSnackBar(viewGroup, customView).setDuration(length!!)
        }
    }
}