package ru.gb.veber.newsapi.common.extentions

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    //FORMAT_DATE_SERVER_RESPONSE "publishedAt":"2023-07-03T12:48:55Z"
    private const val TIME_ZONE = "UTC"
    private const val FORMAT_DATE_SERVER_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val FORMAT_HOUR = "HH:mm"
    private const val FORMAT_DATE_NEWS = "dd.MM.yyyy"
    private const val FORMAT_DATE_UI_DEFAULT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    private const val FORMAT_DATE_DAY = "dd MMMM yyyy, HH:mm"
    private const val FORMAT_KEYS = "dd-MM-yy HH:mm"
    const val FORMAT_DATE = "yyyy-MM-dd"

    fun takeDate(count: Int): Date {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_MONTH, count)
        return currentDate.time
    }

    fun String.toDateFormatDateServerResponse(): Date =
        SimpleDateFormat(FORMAT_DATE_SERVER_RESPONSE, Locale.getDefault()).parse(this) ?: Date()

    fun String.toFormatDateUi(): String =
        try {
            (SimpleDateFormat(FORMAT_DATE_SERVER_RESPONSE, Locale.getDefault()).parse(this)
                ?: Date()).toStringFormatDateUi()
        } catch (exception: Exception) {
            Date().toStringFormatDateUi()
        }


    fun Date.toStringFormatDateUi(): String =
        SimpleDateFormat(FORMAT_DATE_UI_DEFAULT_YYYY_MM_DD_HH_MM, Locale.getDefault()).format(this)

    fun stringFromDataTime(dateString: String): Date =
        SimpleDateFormat(FORMAT_DATE_UI_DEFAULT_YYYY_MM_DD_HH_MM, Locale.getDefault()).parse(
            dateString
        ) ?: Date()

    fun Date.formatKeys(): String = SimpleDateFormat(FORMAT_KEYS, Locale.getDefault()).format(this)
    fun Date.formatHour(): String = SimpleDateFormat(FORMAT_HOUR, Locale.getDefault()).format(this)
    fun Date.formatDate(): String = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(this)

    fun Date.formatDateDay(): String =
        SimpleDateFormat(FORMAT_DATE_DAY, Locale.getDefault()).format(this)

    fun stringFromData(dateString: String) =
        SimpleDateFormat(FORMAT_DATE_SERVER_RESPONSE, Locale.getDefault()).parse(dateString)
            ?: Date()

    fun stringFromDataPiker(dateString: String) =
        SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).parse(dateString) ?: Date()

    fun stringFromDataNews(dateString: String): Date =
        SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).parse(dateString) ?: Date()

    fun Long.toStringDate(formatR: String? = FORMAT_DATE_NEWS): String {
        SimpleDateFormat(formatR, Locale.getDefault()).also {
            it.timeZone = TimeZone.getTimeZone(TIME_ZONE)
            return it.format(this)
        }
    }
}

