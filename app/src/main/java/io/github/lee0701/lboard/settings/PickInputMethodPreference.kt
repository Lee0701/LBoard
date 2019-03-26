package io.github.lee0701.lboard.settings

import android.app.Service
import android.content.Context
import android.support.v7.preference.Preference
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager

class PickInputMethodPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    override fun onClick() {
        val inputMethodManager = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showInputMethodPicker()
    }

}
