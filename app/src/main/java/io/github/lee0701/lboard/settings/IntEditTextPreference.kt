package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet

class IntEditTextPreference(context: Context, attrs: AttributeSet): EditTextPreference(context, attrs) {

    override fun getPersistedString(defaultReturnValue: String?): String? {
        return try {
            getPersistedInt(defaultReturnValue?.toInt() ?: return null).toString()
        } catch(e: NumberFormatException) {
            null
        }
    }

    override fun persistString(value: String?): Boolean {
        return try {
            persistInt(value?.toInt() ?: return false)
        } catch (e: NumberFormatException) {
            false
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        this.text = this.getPersistedInt((defaultValue as String?)?.toInt() ?: 0).toString()
    }
}
