package ru.gb.veber.newsapi.presentation.customview.viewgroup

import android.animation.TypeEvaluator

//Класс который помогает расчитывать конкректное значение нашего типа в зависимости от прогресса анимации (интерполятора)
class StringEvaluator : TypeEvaluator<String> {
    override fun evaluate(fraction: Float, startValue: String?, endValue: String?): String {
        val coercedFraction = fraction.coerceIn(0f, 1f)

        val lengthDiff = endValue?.length!! - startValue?.length!!
        val currentDiff = (lengthDiff * coercedFraction).toInt()

        return if (currentDiff > 0) {
            endValue.substring(0, startValue.length + currentDiff)
        } else {
            startValue.substring(0, startValue.length + currentDiff)
        }
    }
}