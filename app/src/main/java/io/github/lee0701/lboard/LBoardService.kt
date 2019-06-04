package io.github.lee0701.lboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.support.v7.preference.PreferenceManager
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hangul.*
import io.github.lee0701.lboard.hardkeyboard.UniversalHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.UniversalKeyboardLayout
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.layouts.soft.SoftLayout
import io.github.lee0701.lboard.layouts.soft.TwelveSoftLayout
import io.github.lee0701.lboard.layouts.symbols.Symbols
import io.github.lee0701.lboard.softkeyboard.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    private val inputMethods: MutableList<InputMethodSet> = mutableListOf()
    private var currentMethodId: Int = 0
    private var currentModeId: Int = 0
    private val currentMethodSet: InputMethodSet get() = inputMethods[currentMethodId]
    private val currentMethod: InputMethod get() = currentMethodSet.keyModes[currentModeId]

    private var lastMethodId: Int = 0
    private var inputAfterSwitch = false
    private var switchedFromOutside = true

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_common, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_en, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_ko, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_symbols, true)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val theme = BasicSoftKeyboard.THEMES[prefs.getString("common_soft_theme", null)!!]!!
        val height = prefs.getInt("common_soft_height", 0).toFloat()

        val methodEn = WordComposingInputMethod(
                BasicSoftKeyboard(
                        BasicSoftKeyboard.LAYOUTS[prefs.getString("method_en_soft_layout", null)!!]!!,
                        theme,
                        height
                ),
                UniversalHardKeyboard(
                        UniversalHardKeyboard.LAYOUTS[prefs.getString("method_en_hard_layout", null)!!]!!
                )
        )

        val predefinedMethod = PREDEFINED_METHODS[prefs.getString("method_ko_predefined", null)!!]!!

        val combinationTable = predefinedMethod.combinationTable
        val virtualJamoTable = predefinedMethod.virtualJamoTable

        val converter =
                if(predefinedMethod.hangulConverter == PredefinedHangulConverter.DUBEOL) DubeolHangulComposer(combinationTable, virtualJamoTable)
                else SebeolHangulComposer(combinationTable, virtualJamoTable)

        val methodKo = HangulInputMethod(
                BasicSoftKeyboard(
                        predefinedMethod.softLayout ?: BasicSoftKeyboard.LAYOUTS[prefs.getString("method_ko_soft_layout", null)!!]!!,
                        theme,
                        height
                ),
                UniversalHardKeyboard(
                        predefinedMethod.hardLayout
                ),
                converter
        )

        val symbols = AlphabetInputMethod(
                BasicSoftKeyboard(
                        BasicSoftKeyboard.LAYOUTS[prefs.getString("method_symbols_soft_layout", null)!!]!!,
                        theme, height
                ),
                UniversalHardKeyboard(
                        UniversalHardKeyboard.LAYOUTS[prefs.getString("method_symbols_hard_layout", null)!!]!!
                )
        )

        inputMethods += InputMethodSet(methodEn, symbols)
        inputMethods += InputMethodSet(methodKo, symbols)
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentMethod.reset()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        if(finishingInput) switchedFromOutside = true
    }

    private fun reset() {
        currentMethodId = 0
        lastMethodId = 0
        inputAfterSwitch = false
    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {
        currentMethod.reset()

        val last = currentMethodId
        currentModeId = 0

        val fromOutside = switchedFromOutside
        switchedFromOutside = false

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window.attributes.token

        if(inputAfterSwitch && (currentMethodId != lastMethodId || fromOutside)) {
            if(fromOutside) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToPreviousInputMethod()
                else imm.switchToLastInputMethod(token)
            } else {
                currentMethodId = lastMethodId
            }
        } else {
            if(++currentMethodId >= inputMethods.size) {
                currentMethodId = 0
                if(switchBetweenApps) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)  switchToNextInputMethod(false)
                    else imm.switchToNextInputMethod(token, false)
                }
            }
        }
        if(inputAfterSwitch) lastMethodId = last
        inputAfterSwitch = false
        setInputView(currentMethod.initView(this))
    }

    private fun switchKeyMode() {
        currentMethod.reset()
        val last = currentModeId
        if(inputAfterSwitch && currentModeId != 0) {
            currentModeId = 0
        } else {
            if(++currentModeId >= currentMethodSet.keyModes.size)
                currentModeId = 0
        }
        if(inputAfterSwitch) lastMethodId = last
        setInputView(currentMethod.initView(this))
    }

    @Subscribe fun onUpdateView(event: UpdateViewEvent) {
        currentMethod.updateView(this)?.let {
            setInputView(it)
        }
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
        when(event.keyCode) {
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> {
                switchInputMethod(true)
                return
            }
            KeyEvent.KEYCODE_SYM -> {
                switchKeyMode()
                return
            }
        }
        inputAfterSwitch = true
        if(!currentMethod.onKeyPress(event.keyCode)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if(currentInputConnection.getSelectedText(0) != null) currentInputConnection.commitText("", 1)
                else currentInputConnection.deleteSurroundingText(1, 0)
            }
            KeyEvent.KEYCODE_ENTER -> {
                when(currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
                    EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_GO -> {
                        sendDefaultEditorAction(true)
                    }
                    else -> {
                        sendKeyChar('\n')
                    }
                }
            }
            else -> sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, 0).toChar())
        } else currentMethod.onKeyRelease(event.keyCode)
    }

    @Subscribe fun onSoftKeyLongClick(event: SoftKeyLongClickEvent) {
        if(!currentMethod.shift) currentMethod.onKeyPress(KeyEvent.KEYCODE_SHIFT_LEFT)
    }

    @Subscribe fun onSoftKeyFlick(event: SoftKeyFlickEvent) {
        if(!currentMethod.shift) currentMethod.onKeyPress(KeyEvent.KEYCODE_SHIFT_LEFT)
    }

    @Subscribe fun onCompose(event: ComposeEvent) {
        currentInputConnection.setComposingText(event.composing, 1)
    }

    @Subscribe fun onCommitComposing(event: CommitComposingEvent) {
        currentInputConnection.finishComposingText()
    }

    @Subscribe fun onCommitString(event: CommitStringEvent) {
        currentInputConnection.finishComposingText()
        currentInputConnection.commitText(event.string, 1)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return currentMethod.onKeyPress(keyCode) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return currentMethod.onKeyRelease(keyCode) || super.onKeyUp(keyCode, event)
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    enum class PredefinedHangulConverter() {
        NONE, DUBEOL, SEBEOL
    }

    data class PredefinedMethod(
            val softLayout: Layout?,
            val hardLayout: UniversalKeyboardLayout,
            val hangulConverter: PredefinedHangulConverter = PredefinedHangulConverter.NONE,
            val combinationTable: CombinationTable = CombinationTable(mapOf()),
            val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf())
    )

    companion object {
        val PREDEFINED_METHODS = mapOf<String, PredefinedMethod>(
                "dubeol-standard" to PredefinedMethod(null, DubeolHangul.LAYOUT_DUBEOL_STANDARD, PredefinedHangulConverter.DUBEOL, DubeolHangul.COMBINATION_DUBEOL_STANDARD),
                "sebeol-390" to PredefinedMethod(null, SebeolHangul.LAYOUT_SEBEOL_390, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391" to PredefinedMethod(null, SebeolHangul.LAYOUT_SEBEOL_391, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391-strict" to PredefinedMethod(null, SebeolHangul.LAYOUT_SEBEOL_391, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_391),
                "sebeol-shin-original" to PredefinedMethod(null, ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-shin-edit" to PredefinedMethod(null, ShinSebeolHangul.LAYOUT_SHIN_EDIT, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "dubeol-cheonjiin" to PredefinedMethod(TwelveSoftLayout.LAYOUT_12KEY_4COLS, TwelveDubeolHangul.LAYOUT_CHEONJIIN, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_CHEONJIIN),
                "dubeol-naratgeul" to PredefinedMethod(TwelveSoftLayout.LAYOUT_12KEY_4COLS, TwelveDubeolHangul.LAYOUT_NARATGEUL, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_NARATGEUL)
        )
    }

}
