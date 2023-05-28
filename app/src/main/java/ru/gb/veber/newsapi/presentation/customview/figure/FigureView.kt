package ru.gb.veber.newsapi.presentation.customview.figure

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import ru.gb.veber.newsapi.R


@SuppressLint("ResourceAsColor")
class FigureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : View(context, attrs, defStyleAtr) {

    private val paint = Paint()
    private val paintGreen = Paint()
    private val paintCustom = Paint().apply {
        isAntiAlias = true
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val paintText = Paint().apply {
        textSize = 40f
        color = Color.RED
        textAlign = Paint.Align.LEFT
    }

    private val paintRed = Paint().apply { color = Color.RED }

    init {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        paint.color = R.color.static_green
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paintGreen.setColor(Color.GREEN)
        canvas ?: return
        canvas.drawColor(R.color.color_icon_tint)

        canvas.drawOval(0f, 0f, width.toFloat(), height.toFloat(), paint)

        canvas.drawCircle(width / 2f, height / 2f, 100f, paint)

        canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paintCustom)

        canvas.drawPoint(width / 2f, height / 2f, paintRed)

        canvas.translate(100f,100f,)
        canvas.rotate(45f)
        canvas.scale(2f,2f,)
        canvas.drawRect(20f,20f,100f, 100f,paintRed)
        canvas.drawRoundRect(width.toFloat(),height.toFloat(),width / 2f, height / 2f,40f,40f,paintRed)

        val oval2 = RectF(50f, 50f, 150f, 150f)
        canvas.drawArc(oval2,90f,270f,true,paintGreen)

        canvas.drawText("http://developer.alexanderklimov.ru/android", 20f, 200f, paintText)

    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }
}