package ru.gb.veber.newsapi.presentation.customview.viewgroup

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.DynamicLayout
import android.text.Editable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.withStyledAttributes
import ru.gb.veber.newsapi.R
import kotlin.math.max

class CustomViewGroupAndText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : View(context, attrs, defStyleAtr) {

    //TextView использует staticLayout(каждый раз пересчитывается когда изменяем текст) а EditText DynamicLayout

    // При работе с текстовыми layout нужно использовать TextPaint а не Paint
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 80f }

    //Layout, проводящий за нас измерения многострочного текста
    private var textLayout: Layout? = null

    //Здесь живет изменяющийся текст
    private val editable: Editable = SpannableStringBuilder()

    init {
        setBackgroundColor(Color.LTGRAY)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private val animation =
        ValueAnimator.ofObject(StringEvaluator(), "Привет", "Привет! Как дела? Как настроение?")
            .apply {
                duration = 4000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue.toString()
                    //Нужно перерасчитать размеры только если изменилось кол-во строк
                    val prevLineCount = textLayout?.lineCount
                    editable.replace(0, editable.length, animatedValue)

                    if (textLayout?.lineCount != prevLineCount) {
                        requestLayout()
                    }

                    //Перерисовать нужно в любом случии
                    invalidate()
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == oldw) return
        textLayout = DynamicLayout.Builder.obtain(editable, textPaint, w).build()
    }

    override fun onDraw(canvas: Canvas?) {
        textLayout?.draw(canvas)
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

    //Мерием размеры основываясь на них выставляем конкретные размер ы
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Не знаем максимальной ширины текста, займем всю доступную ширину
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = if (widthSpecSize > 0) widthSpecSize else 500
        val height = textLayout?.height ?: (textPaint.descent() - textPaint.ascent()).toInt()
        setMeasuredDimension(width, height)
    }
}