package ru.gb.veber.newsapi.presentation.customview

object Theory {
//Получаем канвас для рисования
//    init {
//    Получения canvas из bitmap
//      val bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888(полная инфа о цветах) (ALPHA_8 легковесная хранит только прозрачность))
//        val canvas = Canvas(bitmap)
//    }
//
//    override fun onDraw(canvas: Canvas?) { //Получения canvas для view
//        super.onDraw(canvas)
//    }
//      override fun dispatchDraw(canvas: Canvas?) { //Получения canvas для ViewGroup(по умолчанию не предназначена для рисования)
//          super.dispatchDraw(canvas)
//      }


//Основные сучности для рисования
    //Pain - с помощью чего рисуем задаем цвета размеры текста
    //Rect  границы
    //Point просто точки x y
    //Path
    //Matrix
    //Bitmap


//Paint внутри можно использовать Static Layout.
//private val paintCustom = Paint().apply {
//    isAntiAlias = true
//    color = Color.GREEN
//    style = Paint.Style.STROKE
//    strokeWidth = 10f
//}
//
//    private val paintText = Paint().apply {
//        textSize = 40f
//        color = Color.RED
//        textAlign = Paint.Align.LEFT
//    }


// Porter duff - способ наолжения одного на другое
// path надоб инструкций для рисования



// Matrix- сушность для трансформации
// Translate сдвиг
// Rotate  поворот
// Skew  наклон
// SetTranslate порядок приенения в начало конец и с стиранием предыдущих

//Анимации
//ObjectAnimator  и ValueAnimator значени 0 -1 например
// AnimatorSet

//Настройки  Листнеры конец начало каждый тик
//мод начало конца анимации , duration длительность количество повторов
// addUpdateListener каждый тик. addListener отслеживание начала конца повтора

//Сущности
//Interpolator Evaluator // как анимации идет оностительно прогресса.  0 1 интерполятор

//MotionEvent touch точи получаем координаты и action


}