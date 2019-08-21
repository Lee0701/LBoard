package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard

class EmptySoftKeyboard: SoftKeyboard {

    private var view: View? = null

    override var shift = 0
    override var alt = 0

    override fun initView(context: Context): View? {
        view = LinearLayout(context)
        return view
    }

    override fun getView(): View? {
        return view
    }

    override fun setLabels(labels: Map<Int, String>) {

    }
}
