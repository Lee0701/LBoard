package io.github.lee0701.lboard.settings

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceManager
import android.util.AttributeSet
import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.dictionary.BuildDictTask
import io.github.lee0701.lboard.dictionary.SQLiteDictionary
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.inputmethod.InputMethodInfo
import io.github.lee0701.lboard.inputmethod.PredictiveInputMethod
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.prediction.SQLiteDictionaryPredictor
import io.github.lee0701.lboard.softkeyboard.EmptySoftKeyboard

class BuildDictionaryPreference(context: Context, attrs: AttributeSet): Preference(context, attrs) {

    private val methodId: Int = attrs.getAttributeIntValue(null, "methodId", 0)
    private val language: String = attrs.getAttributeValue(null, "language")
    private val fileName: String = attrs.getAttributeValue(null, "fileName")

    val pref = PreferenceManager.getDefaultSharedPreferences(context)

    val predefinedMethod = LBoardService.PREDEFINED_METHODS[pref.getString("method_en_predefined", null) ?: ""] ?: LBoardService.PredefinedMethod(LBoardService.SOFT_LAYOUT_UNIVERSAL, Alphabet.LAYOUT_QWERTY)

    val dictionary = SQLiteDictionary.getInstance(context)
    val method = PredictiveInputMethod(
            InputMethodInfo(language = "en", device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = false),
            EmptySoftKeyboard(),
            CommonHardKeyboard(predefinedMethod.hardLayout),
            SQLiteDictionaryPredictor(dictionary, methodId)
    )

    override fun onClick() {
        BuildDictTask(context.assets, dictionary, fileName, method).execute()
    }
}
