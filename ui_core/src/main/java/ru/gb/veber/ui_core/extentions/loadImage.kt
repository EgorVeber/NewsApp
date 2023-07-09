package ru.gb.veber.ui_core.extentions

import android.graphics.drawable.Drawable
import android.widget.ImageView
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
import ru.gb.veber.ui_core.R

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

fun ImageView.loadPicForCard(url: String?, cornerRadius: Float = 25f) {
    loadWithFailure(url, cornerRadius, R.drawable.trdz_no_image, R.drawable.trdz_no_image_alter)
}

fun ImageView.loadPicForTitle(url: String?) {
    loadWithFailure(url, 0f, R.drawable.trdz_no_image_big, R.drawable.trdz_no_image_alter_big)
}

fun ImageView.loadWithFailure(
    url: String?,
    cornerRadius: Float = 25f,
    errorImage: Int,
    emptyImage: Int,
) {
    load(url) {
        placeholder(R.drawable.image_still_loading)
        transformations(RoundedCornersTransformation(cornerRadius))
        listener(
            onError = { _: ImageRequest, error: ErrorResult ->
                val image = if (error.throwable is NullRequestDataException) emptyImage
                else errorImage
                setCoinImage(image, cornerRadius)
            }
        )
    }
}

fun ImageView.setCoinImage(image: Int, cornerRadius: Float = 25f) {
    load(image) {
        crossfade(true)
        placeholder(R.drawable.image_still_loading)
        transformations(RoundedCornersTransformation(cornerRadius))
    }
}