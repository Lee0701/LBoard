package io.github.lee0701.lboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

class LBoardService: InputMethodService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onCreateInputView(): View? {
        return super.onCreateInputView()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

}
