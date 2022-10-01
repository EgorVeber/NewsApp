package ru.gb.veber.newsapi.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.gb.veber.newsapi.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun <T> Single<T>.subscribeDefault(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}


fun Completable.subscribeDefault(): Completable {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun ImageView.loadGlide(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.loading1)
        .error(R.drawable.riaplaceholder)
        .transform(MultiTransformation(RoundedCorners(25)))
        .into(this)
}

fun ImageView.loadGlideNot(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.loading1)
        .error(R.drawable.riaplaceholder)
        .into(this)
}

const val FORMAT_DATE_REQUEST = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val FORMAT_HOUR = "HH:mm"
const val FORMAT_DATE = "yyyy-MM-dd"
const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
const val FORMAT_DATE_DAY = "dd MMMM yyyy, HH:mm"

val EMAIL_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
const val EMAIL_STR: String = "User1@gmail.com"
val PASSWORD_PATTERN: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,20}")
const val PASSWORD_STR: String = "Example Zydfhm2022?"
val LOGIN_PATTERN: Pattern = Pattern.compile("^[A-Z](?=[a-zA-Z0-9._]{4,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")
const val LOGIN_STR: String = "UserName"

fun Date.formatHour(): String = SimpleDateFormat(FORMAT_HOUR, Locale.getDefault()).format(this)
fun Date.formatDate(): String = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(this)
fun Date.formatDateTime(): String =
    SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault()).format(this)

fun Date.formatDateDay(): String =
    SimpleDateFormat(FORMAT_DATE_DAY, Locale.getDefault()).format(this)

fun stringFromData(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_REQUEST, Locale.getDefault()).parse(dateString) ?: Date()

fun takeDate(count: Int): Date {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.DAY_OF_MONTH, count)
    return currentDate.time
}

fun findVideoId(url: String): String {
    return url.substringAfterLast('/').substringBefore('?')
}


fun View.hide(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
    return this
}

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.showSnackBarError(
    text: String,
    actionText: String,
    action: (View) -> Unit,
    length: Int = Snackbar.LENGTH_LONG,
) {
    Snackbar.make(this, text, length)
        .setAction(actionText, action).show()
}

fun Disposable.disposebleBy(bag: CompositeDisposable) {
    bag.add(this)
}

