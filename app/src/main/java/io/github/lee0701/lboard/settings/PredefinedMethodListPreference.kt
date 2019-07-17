package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService

class PredefinedMethodListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val softLayoutKey: String = attrs.getAttributeValue(null, "softLayoutKey")

    init {
        setOnPreferenceChangeListener { _, newValue ->
            val softKeyboardListPreference = findPreferenceInHierarchy(softLayoutKey) as SoftKeyboardListPreference
            val editor = preferenceManager.sharedPreferences.edit()
            softKeyboardListPreference.reloadEntries(newValue as String)
            LBoardService.PREDEFINED_METHODS[newValue]?.let {
                editor.putString(softLayoutKey, it.softLayouts[0].key)
                softKeyboardListPreference.value = it.softLayouts[0].key
            }
            editor.apply()
            true
        }
    }

}
