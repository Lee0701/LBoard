package io.github.lee0701.lboard.event

import android.content.SharedPreferences

class PreferenceChangeEvent(
        val preferences: SharedPreferences
): Event()
