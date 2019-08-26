package io.github.lee0701.lboard.event

import android.view.View

class InputViewChangedEvent(
        methodId: String,
        val requiresInit: Boolean,
        val inputView: View? = null
): InputMethodEvent(methodId)
