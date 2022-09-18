package ru.gb.veber.newsapi.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.gb.veber.newsapi.R
import java.text.SimpleDateFormat
import java.util.*

fun <T> Single<T>.subscribeDefault(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun ImageView.loadGlide(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.loading1)
        .error(R.drawable.plaecehodler2)
        .transform(MultiTransformation(RoundedCorners(25)))
        .into(this)
}
fun ImageView.loadGlideNot(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.loading1)
        .error(R.drawable.plaecehodler2)
        .into(this)
}

const val FORMAT_DATE_REQUEST = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val FORMAT_HOUR = "HH:mm"
const val FORMAT_DATE = "yyyy-MM-dd"
const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
const val FORMAT_DATE_DAY = "dd MMMM yyyy, HH:mm"


fun Date.formatHour(): String = SimpleDateFormat(FORMAT_HOUR, Locale.getDefault()).format(this)
fun Date.formatDate(): String = SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(this)
fun Date.formatDateTime(): String = SimpleDateFormat(FORMAT_DATE_TIME, Locale.getDefault()).format(this)
fun Date.formatDateDay(): String = SimpleDateFormat(FORMAT_DATE_DAY, Locale.getDefault()).format(this)

fun stringFromData(dateString: String) =
    SimpleDateFormat(FORMAT_DATE_REQUEST, Locale.getDefault()).parse(dateString)?:Date()

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

