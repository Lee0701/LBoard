package io.github.lee0701.lboard.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.support.v7.preference.Preference
import android.util.AttributeSet

class EnableInputMethodPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    override fun onClick() {
        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

}
