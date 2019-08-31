package io.github.lee0701.lboard.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.CheckBoxPreference

class IntCheckBoxPreference(context: Context, attrs: AttributeSet): CheckBoxPreference(context, attrs) {
    override fun getPersistedBoolean(defaultReturnValue: Boolean): Boolean {
        return getPersistedInt(if(defaultReturnValue) 1 else 0) != 0
    }

    override fun persistBoolean(value: Boolean): Boolean {
        return persistInt(if(value) 1 else 0)
    }

    override fun getPersistedInt(defaultReturnValue: Int): Int {
        return super.getPersistedInt(defaultReturnValue)
    }

    override fun persistInt(value: Int): Boolean {
        return super.persistInt(value)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        this.isChecked = getPersistedInt((defaultValue as Int? ?: 0)) != 0
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }
}
