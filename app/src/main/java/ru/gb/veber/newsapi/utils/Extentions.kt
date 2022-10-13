package ru.gb.veber.newsapi.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

fun BottomSheetBehavior<ConstraintLayout>.collapsed() {
    state = BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<ConstraintLayout>.expanded() {
    state = BottomSheetBehavior.STATE_EXPANDED
}


fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

// Расширяем функционал вью для скрытия клавиатуры
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun ImageView.loadGlide(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.newsplaceholder)
        .error(R.drawable.newsplaceholder)
        .timeout(2000)
        .transform(MultiTransformation(RoundedCorners(25)))
        .into(this)
}

fun ImageView.loadGlideNot(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.newsplaceholder)
        .error(R.drawable.newsplaceholder)
        .timeout(2000)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }
        }).into(this);
}

const val FORMAT_DATE_REQUEST = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val FORMAT_HOUR = "HH:mm"
const val FORMAT_DATE = "yyyy-MM-dd"
const val FORMAT_DATE_NEWS = "dd.MM.yyyy"
const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
const val FORMAT_DATE_DAY = "dd MMMM yyyy, HH:mm"

val EMAIL_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
const val EMAIL_STR: String = "User1@gmail.com"
val PASSWORD_PATTERN: Pattern =
    Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,20}")
const val PASSWORD_STR: String = "Example Zydfhm2022?"
val LOGIN_PATTERN: Pattern =
    Pattern.compile("^[A-Z](?=[a-zA-Z0-9._]{4,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")
const val LOGIN_STR: String = "UserName"

fun Date.formatHour(): String = SimpleDateFormat(FORMAT_HOUR, Locale.getDefault()).format(this)
fun Date.formatDate(): String = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(this)
fun Date.formatDateNews(): String =
    SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).format(this)

fun Date.formatDateTime(): String =
    SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault()).format(this)

fun Date.formatDateDay(): String =
    SimpleDateFormat(FORMAT_DATE_DAY, Locale.getDefault()).format(this)

fun stringFromData(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_REQUEST, Locale.getDefault()).parse(dateString) ?: Date()

//эТО из за календаря потом убрать надо
fun stringFromDataPiker(dateString: String) =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()

fun stringFromDataNews(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_NEWS, Locale.getDefault()).parse(dateString) ?: Date()


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


fun String.checkLogin(): String {
    return if (this.length >= 7) {
        this.substring(0, 7)
    } else {
        this
    }
}

val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun initDatePicker(date: Date, datePicker: DatePicker) {
    Calendar.getInstance().also {
        it.time = date
        datePicker.init(it[Calendar.YEAR], it[Calendar.MONTH], it[Calendar.DAY_OF_MONTH], null)
    }
}

fun getDateFromDatePicker(datePicker: DatePicker): Date {
    return Calendar.getInstance().apply {
        this[Calendar.YEAR] = datePicker.year
        this[Calendar.MONTH] = datePicker.month
        this[Calendar.DAY_OF_MONTH] = datePicker.dayOfMonth
    }.time
}

