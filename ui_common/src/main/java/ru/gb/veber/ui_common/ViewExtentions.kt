package ru.gb.veber.ui_common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun View.showText(string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
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

fun Fragment.showKeyboard() {
    requireActivity().showKeyboard()
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Context.showKeyboard(view: View) {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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

fun String.getCutLogin(): String  =
    if (length >= 7) {
        substring(0, 7)
    } else {
        this
    }

