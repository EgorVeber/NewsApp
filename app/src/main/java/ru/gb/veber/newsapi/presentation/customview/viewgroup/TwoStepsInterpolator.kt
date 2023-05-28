package ru.gb.veber.newsapi.presentation.customview.viewgroup

import android.os.Build
import android.view.animation.BaseInterpolator
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class TwoStepsInterpolator : BaseInterpolator() {
    //input прогресс анимации во времяни
    //return прогресс анимируемого значения. 0-> начальное значение, 1 -> конечное значение
    override fun getInterpolation(input: Float): Float {
        return when {
            input < 0.3f -> 0.5f * (input / 0.3f)
            input > 0.7f -> 0.5f + (0.5f * (input - 0.7f) / 0.3f)
            else -> 0.5f
        }
    }
}