package io.github.lee0701.lboard.candidates

import android.content.Context
import android.content.SharedPreferences
import android.view.View

interface CandidatesViewManager {

    fun initView(context: Context): View?
    fun getView(): View?

    fun setPreferences(pref: SharedPreferences) {

    }

    fun init()
    fun destroy()

}
