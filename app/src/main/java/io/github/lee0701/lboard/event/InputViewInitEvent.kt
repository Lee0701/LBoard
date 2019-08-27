package io.github.lee0701.lboard.event

import android.content.Context

class InputViewInitEvent(
        val context: Context,
        val requiresInit: Boolean = false
): Event()
