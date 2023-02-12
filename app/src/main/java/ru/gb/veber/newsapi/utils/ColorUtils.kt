package ru.gb.veber.newsapi.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object ColorUtils {
    private val typedValue: TypedValue = TypedValue()

    fun Fragment.getColor(@ColorRes color: Int): Int =
        ContextCompat.getColor(this.requireContext(), color)

    fun Fragment.getDrawable(@DrawableRes drawableRes: Int): Drawable? =
        ContextCompat.getDrawable(this.requireContext(), drawableRes)

    fun Fragment.getColorAttr(@AttrRes attrRes: Int, needResId: Boolean): Int {
        this.requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return if (needResId) typedValue.resourceId else typedValue.data
    }

    fun Activity.getColorRes(@ColorRes color: Int): Int =
        ContextCompat.getColor(this, color)

    fun Activity.getDrawableRes(@DrawableRes drawableRes: Int): Drawable? =
        ContextCompat.getDrawable(this, drawableRes)

    fun Activity.getColorAttr(@AttrRes attrRes: Int, needResId: Boolean): Int {
        this.theme.resolveAttribute(attrRes, typedValue, true)
        return if (needResId) typedValue.resourceId else typedValue.data
    }
}