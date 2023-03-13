package ru.gb.veber.newsapi.presentation.topnews.fragment.filteranimation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BehaviorActionButtonCancel(context: Context, attr: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attr) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        return (dependency is FloatingActionButton)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        child.y = dependency.y
        child.x = MARGIN_START
        return super.onDependentViewChanged(parent, child, dependency)
    }

    companion object {
        const val MARGIN_START = 50F
    }
}
