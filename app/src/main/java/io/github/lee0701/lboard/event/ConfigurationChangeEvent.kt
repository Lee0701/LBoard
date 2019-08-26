package io.github.lee0701.lboard.event

import android.content.res.Configuration
import android.view.inputmethod.EditorInfo

class ConfigurationChangeEvent(
        val configuration: Configuration
): Event()
