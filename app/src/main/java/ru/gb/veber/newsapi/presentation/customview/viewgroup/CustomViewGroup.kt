package ru.gb.veber.newsapi.presentation.customview.viewgroup

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.withStyledAttributes
import ru.gb.veber.newsapi.R
import kotlin.math.max

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : ViewGroup(context, attrs, defStyleAtr) {

    private val firstChild: View?
        get() = if (childCount > 0) getChildAt(0) else null

    private val secondChild: View?
        get() = if (childCount > 1) getChildAt(1) else null

    private var verticalOffset = 0

    //Обычный аниматор
//    private val animation = ValueAnimator.ofFloat(0f, 1f).apply {
//        duration = 4000L
//        repeatCount = ValueAnimator.INFINITE // Бесконечно
//        repeatMode = ValueAnimator.REVERSE // обратка
//        addUpdateListener { animator ->
//            val animatedValue = animator.animatedValue as Float
//            val startValue = "Привет"
//            val endValue = "Привет! Как дела? Как настроение?"
//
//            val lengthDiff = endValue.length - startValue.length
//            val currentDiff = (lengthDiff * animatedValue).toInt()
//            val result = if (currentDiff > 0) {
//                endValue.substring(0, startValue.length + currentDiff)
//            } else {
//                startValue.substring(0, startValue.length + currentDiff)
//            }
//
//            (firstChild as TextView).text = result
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private val animation =
        ValueAnimator.ofObject(StringEvaluator(), "Привет", "Привет! Как дела? Как настроение?")
            .apply {
                duration = 4000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = TwoStepsInterpolator()
                addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue.toString()
                    (firstChild as TextView).text = animatedValue //каждый раз request layout вызывается
                }
            }
    init {
        context.withStyledAttributes(attrs, R.styleable.CustomViewGroup) {
            verticalOffset = getDimensionPixelOffset(R.styleable.CustomViewGroup_verticalOffset, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animation.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animation.cancel()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        //Условно если тяжелая анимация то лучше сделать стартовать и заканчивать.

    }

    //Мерием размеры основываясь на них выставляем конкретные размер ы
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        checkChildCount()
        firstChild?.let { measureChild(it, widthMeasureSpec) }
        secondChild?.let { measureChild(it, widthMeasureSpec) }

        val firstWight = firstChild?.measuredWidth
        val firstHeight = firstChild?.measuredHeight
        val secondWight = secondChild?.measuredWidth
        val secondHeight = secondChild?.measuredHeight

        val wightMode = MeasureSpec.getMode(widthMeasureSpec)
        val wightSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val childrenOnSameLine =
            firstWight!! + secondWight!! < wightSize || wightMode == MeasureSpec.UNSPECIFIED

        val wight = when (wightMode) {
            MeasureSpec.UNSPECIFIED -> firstWight!! + secondWight!!
            MeasureSpec.AT_MOST -> wightSize
            MeasureSpec.EXACTLY -> if (childrenOnSameLine) {
                firstWight + secondWight
            } else {
                max(firstWight, secondWight)
            }
            else -> error("Unreachable")
        }

        val height = if (childrenOnSameLine) {
            max(firstHeight!!, secondHeight!!)
        } else {
            firstHeight!! + secondHeight!! + verticalOffset
        }

        setMeasuredDimension(wight + paddingLeft + paddingRight, height)
    }

    //распологаем child внутри
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        firstChild?.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + (firstChild?.measuredWidth ?: 0),
            paddingTop + (firstChild?.measuredHeight ?: 0))
        secondChild?.layout(
            r - l - paddingRight - (secondChild?.measuredWidth ?: 0),
            b - t - paddingBottom - (secondChild?.measuredHeight ?: 0),
            r - l - paddingRight,
            b - t - paddingBottom)
    }

    private fun measureChild(child: View, widthMeasureSpec: Int) {
        val specSize = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val childHeightSpec = MeasureSpec.makeMeasureSpec(specSize, MeasureSpec.UNSPECIFIED)
        val childWidthSpec = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> widthMeasureSpec
            MeasureSpec.AT_MOST -> widthMeasureSpec
            MeasureSpec.EXACTLY -> MeasureSpec.makeMeasureSpec(specSize, MeasureSpec.AT_MOST)
            else -> error("Unreachable")
        }
        child.measure(childWidthSpec, childHeightSpec)
    }

    private fun checkChildCount() {
        if (childCount > 2) error("Только 2")
    }

    //Чтобы задавать margin надо привести layout params def к margin
    // Из верстки
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    //Превращаем другие LayoutParams в наши
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    //Добавили из кода
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }
}