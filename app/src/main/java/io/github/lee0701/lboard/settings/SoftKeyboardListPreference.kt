package io.github.lee0701.lboard.settings

import android.content.Context
import android.os.Handler
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.event.ResetViewEvent
import org.greenrobot.eventbus.EventBus

class SoftKeyboardListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val predefinedMethodKey: String = attrs.getAttributeValue(null, "predefinedMethodKey")
    private val handler: Handler = Handler()

    init {
        setOnPreferenceChangeListener { _, _ ->
            handler.post { EventBus.getDefault().post(ResetViewEvent()) }
            true
        }
        reloadEntries()
    }

    fun reloadEntries(predefinedMethodName: String) {
        val predefinedMethod = LBoardService.PREDEFINED_METHODS[predefinedMethodName]
        predefinedMethod?.let { method ->
            val softLayoutKeys = method.softLayouts.map { it.key }
            val softLayoutStrings = method.softLayouts.map { context?.resources?.getString(it.nameStringKey) }
            entries = softLayoutStrings.toTypedArray()
            entryValues = softLayoutKeys.toTypedArray()
        }
    }

    fun reloadEntries() {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val predefinedMethodName = pref.getString(predefinedMethodKey, "") ?: ""
        reloadEntries(predefinedMethodName)
    }

}
