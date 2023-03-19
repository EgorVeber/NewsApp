package ru.gb.veber.newsapi.presentation.searchnews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout

class BehaviorTitleSearch(context: Context, attr: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attr) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        return (dependency is AppCompatImageView)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        child.x = (dependency.x + dependency.width + MARGIN_LEFT)
        return super.onDependentViewChanged(parent, child, dependency)
    }
    companion object{
        const val MARGIN_LEFT= 30
    }
}