package ru.gb.veber.newsapi.common.extentions

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {

    private const val TIME_ZONE = "UTC"
    private const val FORMAT_DATE_SERVER_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val FORMAT_DATE_DEFAULT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    private const val FORMAT_DATE_DD_MMMM_YYYY_HH_MM = "dd MMMM yyyy, HH:mm"
    private const val FORMAT_DATE_DD_MM_YYYY = "dd.MM.yyyy"
    private const val FORMAT_DATE_DD_MM_YY_HH_MM = "dd-MM-yy HH:mm"
    private const val FORMAT_DATE_YYYY_MM_DD = "yyyy-MM-dd"
    private const val FORMAT_DATE_HH_MM = "HH:mm"


    fun String.toFormatDateDefault(): String =
        try {
            toDateFormatDateServerResponse().toStringFormatDateDefault()
        } catch (exception: Exception) {
            Date().toStringFormatDateDefault()
        }

    fun String.toFormatDateNoTime(): String =
        try {
            toDateFormatDate().toStringFormatDateYearMonthDay()
        } catch (exception: Exception) {
            Date().toStringFormatDateYearMonthDay()
        }

    fun String.toFormatDateDayMouthYearHoursMinutes(): String =
        try {
            toDateFormatDate().toStringFormatDateDayMouthYearHoursMinutes()
        } catch (exception: Exception) {
            Date().toStringFormatDateDayMouthYearHoursMinutes()
        }

    fun String.toDateFormatDateServerResponse(): Date {
        return try {
            SimpleDateFormat(FORMAT_DATE_SERVER_RESPONSE, Locale.getDefault()).parse(this) ?: Date()
        } catch (exception: Exception) {
            Date()
        }
    }

    private fun String.toDateFormatDate(): Date {
        return try {
            getSimpleDateFormat(FORMAT_DATE_DEFAULT_YYYY_MM_DD_HH_MM).parse(this) ?: Date()
        } catch (exception: Exception) {
            Date()
        }
    }

    fun String.toDateFormatDateSeparator(): Date {
        return try {
            getSimpleDateFormat(FORMAT_DATE_DD_MM_YYYY).parse(this) ?: Date()
        } catch (exception: Exception) {
            Date()
        }
    }

    fun Date.toStringFormatDateDefault(): String =
        getSimpleDateFormat(FORMAT_DATE_DEFAULT_YYYY_MM_DD_HH_MM).format(this)

    fun Date.toStringFormatDateHourMinutes(): String =
        getSimpleDateFormat(FORMAT_DATE_HH_MM).format(this)

    fun Date.toStringFormatDateYearMonthDay(): String =
        getSimpleDateFormat(FORMAT_DATE_YYYY_MM_DD).format(this)

    fun Date.toStringFormatDateDayMonthYearHourMinutes(): String =
        getSimpleDateFormat(FORMAT_DATE_DD_MM_YY_HH_MM).format(this)

    private fun Date.toStringFormatDateDayMouthYearHoursMinutes(): String =
        getSimpleDateFormat(FORMAT_DATE_DD_MMMM_YYYY_HH_MM).format(this)

    fun Date.equalsByFormat(
        date: Date,
        simpleDateFormat: SimpleDateFormat = getSimpleDateFormat(FORMAT_DATE_YYYY_MM_DD),
    ): Boolean = simpleDateFormat.format(this).equals(simpleDateFormat.format(date))

    fun Long.toStringFormatDateSeparator(): String =
        getSimpleDateFormat(FORMAT_DATE_DD_MM_YYYY).format(this)

    fun getCurrentDateMinusDay(dayCount: Int): Date =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, dayCount * -1) }.time

    private fun getSimpleDateFormat(format: String) =
        SimpleDateFormat(format, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(TIME_ZONE)
        }
}

