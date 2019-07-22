package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService

class SoftKeyboardListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val predefinedMethodKey: String = attrs.getAttributeValue(null, "predefinedMethodKey")
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        reloadEntries()
    }

    fun reloadEntries(predefinedMethodName: String, modeName: String) {
        val predefinedMethod = LBoardService.PREDEFINED_METHODS[predefinedMethodName]
        val mode = LBoardService.getMode(modeName)
        predefinedMethod?.let { method ->
            val softLayouts = method.softLayouts.filter { mode.contains(it) }
            val softLayoutKeys = softLayouts.map { it.key }
            val softLayoutStrings = softLayouts.map { context?.resources?.getString(it.nameStringKey) }
            entries = softLayoutStrings.toTypedArray()
            entryValues = softLayoutKeys.toTypedArray()
        }
    }

    fun reloadEntries(predefinedMethodName: String) {
        val modeName = pref.getString("common_soft_mode", null) ?: "mobile"
        reloadEntries(predefinedMethodName, modeName)
    }

    fun reloadEntries() {
        val predefinedMethodName = pref.getString(predefinedMethodKey, "") ?: ""
        reloadEntries(predefinedMethodName)
    }

}
