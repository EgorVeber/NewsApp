package ru.gb.veber.newsapi.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class LifeCycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : View(context, attrs, defStyleAtr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // ->requestLayout() вызвает пересчет размеров не гарантирует перерисовку
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //widthMeasureSpec и heightMeasureSpec ограничения а не размеры и 3 мода для
        // ->requestLayout()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Изменения размеров
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //Для viewGroup расположение детей
        //-> invalidate() вызывает перерисовку. // это флаги пожтому выполнится оин раз
    }

    override fun onDraw(canvas: Canvas?) {
        //-> invalidate()
        super.onDraw(canvas)
        //рисование
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        //ViewGroup рисование
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //размер измениося
    }

    //Обработка тачкй
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)//от ребенка к родителю
        //Обработка касаний True если обработали event
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(event)//от родителя к ребенку
        //Обработка касаний True если хотим дальше получать евенты
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)//Аналог до onTouchEvent
    }

//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {  ViewGroup
//        return super.onInterceptTouchEvent(ev)
     //true если мы хотим забрать обработку от детей. event детям не пойдет.
//    }

}