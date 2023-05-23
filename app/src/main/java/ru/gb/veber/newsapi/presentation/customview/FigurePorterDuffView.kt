package ru.gb.veber.newsapi.presentation.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.applyCanvas
import ru.gb.veber.newsapi.R


@SuppressLint("ResourceAsColor")
class FigurePorterDuffView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : View(context, attrs, defStyleAtr) {

    private val paintCustom = Paint().apply {
        color = Color.GREEN
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }


    private val paintCustom2 = Paint().apply {
        color = Color.RED
    }

    private val paintText = Paint().apply {
        textSize = 40f
        color = Color.RED
        textAlign = Paint.Align.LEFT
    }

    private val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888).applyCanvas {
        drawCircle(100f, 100f, 100f, paintCustom)
        drawCircle(200f, 200f, 100f, paintCustom2)
    }

    private val path: Path = Path().apply {
        moveTo(10f,10f)
        lineTo(300f,10f)
        lineTo(300f,300f)
        close()
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        canvas.drawColor(R.color.color_icon_tint)
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())
        canvas.drawPath(path,paintCustom2)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }
}