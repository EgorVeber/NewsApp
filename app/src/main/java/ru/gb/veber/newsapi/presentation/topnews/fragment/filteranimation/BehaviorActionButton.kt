package ru.gb.veber.newsapi.presentation.topnews.fragment.filteranimation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout

class BehaviorActionButton(context: Context, attr: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attr) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        return (dependency is ConstraintLayout)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View,
    ): Boolean {
        child.y = (dependency.height - child.height - BIAS_VERTICAL).toFloat()
        child.x = (dependency.width - child.height - MARGIN_END).toFloat()
        return super.onDependentViewChanged(parent, child, dependency)
    }

    companion object {
        const val BIAS_VERTICAL = 100
        const val MARGIN_END = 50
    }
}
