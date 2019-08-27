package io.github.lee0701.lboard.event

import android.view.View
import io.github.lee0701.lboard.inputmethod.InputMethodInfo

class InputViewChangeEvent(
        methodInfo: InputMethodInfo,
        val inputView: View? = null
): InputMethodEvent(methodInfo)
