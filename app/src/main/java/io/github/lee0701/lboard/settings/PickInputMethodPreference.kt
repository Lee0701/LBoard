package io.github.lee0701.lboard.settings

import android.app.Service
import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.preference.Preference

class PickInputMethodPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    override fun onClick() {
        val inputMethodManager = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showInputMethodPicker()
    }

}
