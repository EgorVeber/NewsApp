package ru.gb.veber.newsapi.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class CustomAutoCompleteTextView : MaterialAutoCompleteTextView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!,
        attrs,
        defStyle) {
    }

    override fun enoughToFilter(): Boolean {
        return true
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        //чтоб не закрывала при беке
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing) {
            val inputManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager.hideSoftInputFromWindow(findFocus().windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            ) {
                return true
            }
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if(focused){
            this.showDropDown()
        }
    }


//
//    override fun setOnKeyListener(l: OnKeyListener?) {
//        this.hideKeyboard()
//    }
}