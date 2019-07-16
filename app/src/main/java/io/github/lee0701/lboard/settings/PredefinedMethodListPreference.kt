package io.github.lee0701.lboard.settings

import android.content.Context
import android.os.Handler
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.event.ResetViewEvent
import org.greenrobot.eventbus.EventBus

class PredefinedMethodListPreference(context: Context, attrs: AttributeSet): ListPreference(context, attrs) {

    private val softLayoutKey: String = attrs.getAttributeValue(null, "softLayoutKey")
    private val handler: Handler = Handler()

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
            handler.post { EventBus.getDefault().post(ResetViewEvent()) }
            true
        }
    }

}
