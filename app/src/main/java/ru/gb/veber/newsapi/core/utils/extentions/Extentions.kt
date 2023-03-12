package ru.gb.veber.newsapi.core.utils.extentions

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

//FORMAT DATE
const val FORMAT_DATE_REQUEST = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val FORMAT_HOUR = "HH:mm"
const val FORMAT_DATE = "yyyy-MM-dd"
const val FORMAT_DATE_NEWS = "dd.MM.yyyy"
const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
const val FORMAT_DATE_DAY = "dd MMMM yyyy, HH:mm"
const val TIME_ZONE = "UTC"

//EXAMPLE ERROR
const val LOGIN_STR: String = "UserName"
const val EMAIL_STR: String = "User1@gmail.com"
const val PASSWORD_STR: String = "Example Zydfhm2022?"

val EMAIL_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
val PASSWORD_PATTERN: Pattern =
    Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,20}")
val LOGIN_PATTERN: Pattern =
    Pattern.compile("^[A-Z](?=[a-zA-Z0-9._]{4,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")

fun takeDate(count: Int): Date {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.DAY_OF_MONTH, count)
    return currentDate.time
}

fun Date.formatHour(): String = SimpleDateFormat(FORMAT_HOUR, Locale.getDefault()).format(this)
fun Date.formatDate(): String = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(this)
fun Date.formatDateTime(): String =
    SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault()).format(this)

fun Date.formatDateDay(): String =
    SimpleDateFormat(FORMAT_DATE_DAY, Locale.getDefault()).format(this)

fun stringFromData(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_REQUEST, Locale.getDefault()).parse(dateString) ?: Date()

fun stringFromDataTime(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault()).parse(dateString) ?: Date()

fun stringFromDataPiker(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).parse(dateString) ?: Date()

fun stringFromDataNews(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).parse(dateString) ?: Date()


fun String.checkLogin(): String {
    return if (this.length >= 7) {
        this.substring(0, 7)
    } else {
        this
    }
}
