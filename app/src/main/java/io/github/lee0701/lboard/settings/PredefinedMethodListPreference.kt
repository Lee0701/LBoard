package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.R

class PredefinedMethodListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val softLayoutKey: String = attrs.getAttributeValue(null, "softLayoutKey")
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        setOnPreferenceChangeListener { _, newValue ->
            val softKeyboardListPreference = findPreferenceInHierarchy(softLayoutKey) as SoftKeyboardListPreference
            val editor = pref.edit()
            val modeName = pref.getString("common_soft_mode", null) ?: "mobile"
            val mode = LBoardService.getMode(modeName)
            LBoardService.PREDEFINED_METHODS[newValue]?.let {
                val softLayouts = it.softLayouts.filter { mode.contains(it) }
                if(softLayouts.isEmpty()) {
                    AlertDialog.Builder(context)
                            .setMessage(context.resources.getString(R.string.msg_unsupported_layout_for_mode, modeName))
                            .setPositiveButton(android.R.string.ok) { _, _ -> }
                            .create().show()
                    return@setOnPreferenceChangeListener false
                }
                editor.putString(softLayoutKey, softLayouts[0].key)
                softKeyboardListPreference.value = softLayouts[0].key
            }
            editor.apply()
            softKeyboardListPreference.reloadEntries(newValue as String, modeName)
            return@setOnPreferenceChangeListener true
        }
    }

}
