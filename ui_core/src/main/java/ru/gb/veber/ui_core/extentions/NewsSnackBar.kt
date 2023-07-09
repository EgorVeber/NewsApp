package ru.gb.veber.ui_core.extentions

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.gb.veber.ui_core.view.NewsSnackBar

fun Fragment.showSnackBar(text: String, length: Int? = Snackbar.LENGTH_LONG) {
    NewsSnackBar.make(this.requireActivity().findViewById(android.R.id.content), text, length)
        .show()
}

fun Activity.showSnackBar(text: String, length: Int? = Snackbar.LENGTH_LONG) {
    NewsSnackBar.make(this.findViewById(android.R.id.content), text, length).show()
}
