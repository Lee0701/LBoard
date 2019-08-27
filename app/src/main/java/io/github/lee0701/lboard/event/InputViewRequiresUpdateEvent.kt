package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.inputmethod.InputMethodInfo

class InputViewRequiresUpdateEvent(
        methodInfo: InputMethodInfo
): InputMethodEvent(methodInfo)
