package ru.gb.veber.newsapi.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.databinding.SnackbarViewBinding

class SnackBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAtr), ContentViewCallback {

    private val binding: SnackbarViewBinding

    override fun animateContentIn(delay: Int, duration: Int) {}
    override fun animateContentOut(delay: Int, duration: Int) {}

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.snackbar_view, this, true)
        binding = SnackbarViewBinding.bind(this)
    }

    fun setText(text: String) {
        binding.textSnachBar.text = text
    }
}