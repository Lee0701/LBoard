package io.github.lee0701.lboard.event

import android.view.View

class InputViewChangeEvent(
        methodId: String,
        val inputView: View? = null
): InputMethodEvent(methodId)
