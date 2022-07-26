package ru.gb.veber.newsapi.view.search.searchnews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout

class BehaviorRecycler(context: Context, attr: AttributeSet? = null) :
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
        child.y = (dependency.y + dependency.height * BIAS_VERTICAL)
        return super.onDependentViewChanged(parent, child, dependency)
    }
    companion object{
        const val BIAS_VERTICAL = 1.5F
    }
}