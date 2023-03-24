package ru.gb.veber.newsapi.common.extentions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.NullRequestDataException
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.NewsSnackBar


fun Fragment.showSnackBar(text: String, length: Int? = Snackbar.LENGTH_LONG) {
    NewsSnackBar.make(this.requireActivity().findViewById(android.R.id.content), text, length)
        .show()
}

fun Activity.showSnackBar(text: String, length: Int? = Snackbar.LENGTH_LONG) {
     NewsSnackBar.make(this.findViewById(android.R.id.content), text, length).show()
}

fun View.showText(string: String) {
    Toast.makeText(this.context, string, Toast.LENGTH_SHORT).show()
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
        .placeholder(R.drawable.chipmunk)
        .error(R.drawable.chipmunk)
        .transform(MultiTransformation(RoundedCorners(25)))
        .into(this)
}

fun ImageView.loadGlideNot(url: String?) {
    Glide.with(context).load(url)
        .placeholder(R.drawable.chipmunk)
        .error(R.drawable.chipmunk)
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

fun ImageView.loadPicForCard(url: String?,cornerRadius: Float=25f) {
    loadWithFailure(url,cornerRadius,R.drawable.trdz_no_image,R.drawable.trdz_no_image_alter)
}

fun ImageView.loadPicForTitle(url: String?,cornerRadius: Float = 25f) {
    loadWithFailure(url,cornerRadius,R.drawable.trdz_no_image_big,R.drawable.trdz_no_image_alter_big)
}

fun ImageView.loadWithFailure(url: String?,cornerRadius: Float = 25f, errorImage: Int, emptyImage: Int,)  {
    load(url) {
        placeholder(R.drawable.image_still_loading)
        transformations(RoundedCornersTransformation(cornerRadius))
        listener(
            onError = { _: ImageRequest, error: ErrorResult ->
                val image = if (error.throwable is NullRequestDataException) emptyImage
                else errorImage
                setCoinImage(image,cornerRadius)
            }
        )
    }
}

fun ImageView.setCoinImage(image: Int,cornerRadius: Float = 25f) {
    load(image) {
        crossfade(true)
        placeholder(R.drawable.image_still_loading)
        transformations(RoundedCornersTransformation(cornerRadius))
    }
}

fun BottomSheetBehavior<ConstraintLayout>.collapsed() {
    state = BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<ConstraintLayout>.half() {
    state = BottomSheetBehavior.STATE_HALF_EXPANDED
}

fun BottomSheetBehavior<ConstraintLayout>.expanded() {
    state = BottomSheetBehavior.STATE_EXPANDED
}

