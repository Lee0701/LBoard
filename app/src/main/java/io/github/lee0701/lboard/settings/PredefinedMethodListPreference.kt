package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService

class PredefinedMethodListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val softLayoutKey: String = attrs.getAttributeValue(null, "softLayoutKey")
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        setOnPreferenceChangeListener { _, newValue ->
            val softKeyboardListPreference = findPreferenceInHierarchy(softLayoutKey) as SoftKeyboardListPreference
            val editor = pref.edit()
            val mode = LBoardService.getMode(pref.getString("common_soft_mode", null) ?: "mobile")
            softKeyboardListPreference.reloadEntries(newValue as String)
            LBoardService.PREDEFINED_METHODS[newValue]?.let {
                val softLayouts = it.softLayouts.filter { mode.contains(it) }
                editor.putString(softLayoutKey, softLayouts[0].key)
                softKeyboardListPreference.value = softLayouts[0].key
            }
            editor.apply()
            true
        }
    }

}
