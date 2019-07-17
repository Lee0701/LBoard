package io.github.lee0701.lboard

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
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
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.layouts.soft.MiniSoftLayout
import io.github.lee0701.lboard.layouts.soft.SoftLayout
import io.github.lee0701.lboard.layouts.soft.TabletSoftLayout
import io.github.lee0701.lboard.layouts.soft.TwelveSoftLayout
import io.github.lee0701.lboard.layouts.symbols.Symbols
import io.github.lee0701.lboard.softkeyboard.*
import io.github.lee0701.lboard.softkeyboard.EmptySoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val softinputMethods: MutableList<InputMethod> = mutableListOf()
    private val physicalInputMethods: MutableList<InputMethod> = mutableListOf()

    private var physicalKeyboard: Boolean = false
    private var currentMethodId: Int = 0
    private val currentMethod: InputMethod get() =
        if(physicalKeyboard) physicalInputMethods[currentMethodId] else softinputMethods[currentMethodId]

    var inputAfterSwitch: Boolean = false

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_common, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_en, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_ko, true)

        reloadPreferences()

        physicalKeyboard = resources.configuration.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES

    }

    private fun reloadPreferences() {
        softinputMethods.clear()
        physicalInputMethods.clear()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val theme = BasicSoftKeyboard.THEMES[prefs.getString("common_soft_theme", null)!!]!!
        val height = prefs.getInt("common_soft_height", 0).toFloat()
        val labels = prefs.getBoolean("common_soft_labels", true)
        val marginHorizontal = prefs.getInt("common_soft_margin_horizontal", 0)
        val marginBottom = prefs.getInt("common_soft_margin_bottom", 0)

        val repeatRate = prefs.getInt("common_soft_repeat_rate", 0)
        val longClickDelay = prefs.getInt("common_soft_long_click_delay", 0)

        run {
            val predefinedMethod = PREDEFINED_METHODS[prefs.getString("method_en_predefined", null)!!]!!

            val softLayout = BasicSoftKeyboard.LAYOUTS[prefs.getString("method_en_soft_layout", null)!!]!!
            val symbolsLayout = predefinedMethod.symbolLayout ?: CommonHardKeyboard.LAYOUTS[prefs.getString("method_en_symbols_hard_layout", null)!!]!!
            val hardLayout = predefinedMethod.hardLayout

            val methodEn = WordComposingInputMethod(
                    BasicSoftKeyboard(softLayout, theme, height, labels,
                            repeatRate, longClickDelay, marginHorizontal, marginHorizontal, marginBottom),
                    CommonHardKeyboard(symbolsLayout + hardLayout)
            )

            softinputMethods += methodEn
        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[prefs.getString("method_ko_predefined", null)!!]!!

            val timeout = prefs.getInt("method_ko_timeout", 0)

            val softLayout = BasicSoftKeyboard.LAYOUTS[prefs.getString("method_ko_soft_layout", null)!!]!!
            val symbolsLayout = predefinedMethod.symbolLayout ?: CommonHardKeyboard.LAYOUTS[prefs.getString("method_ko_symbols_hard_layout", null)!!]!!

            val combinationTable = predefinedMethod.combinationTable
            val virtualJamoTable = predefinedMethod.virtualJamoTable

            val converter =
                    when(predefinedMethod.hangulConverter) {
                        PredefinedHangulConverter.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable)
                        PredefinedHangulConverter.DUBEOL_SINGLE_VOWEL -> SingleVowelDubeolHangulComposer(combinationTable, virtualJamoTable)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable)
                    }

            val methodKo = HangulInputMethod(
                    BasicSoftKeyboard(softLayout, theme, height, labels,
                            repeatRate, longClickDelay, marginHorizontal, marginHorizontal, marginBottom),
                    CommonHardKeyboard(symbolsLayout + predefinedMethod.hardLayout),
                    converter,
                    timeout
            )

            softinputMethods += methodKo
        }

        run {
            val hardLayout = CommonHardKeyboard.LAYOUTS[prefs.getString("method_en_physical_hard_layout", null)!!]!!
            val symbolsLayout = CommonHardKeyboard.LAYOUTS[prefs.getString("method_en_physical_symbols_hard_layout", null)!!]!!

            val methodEn = AlphabetInputMethod(
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(symbolsLayout + hardLayout)
            )

            physicalInputMethods += methodEn
        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[prefs.getString("method_ko_physical_predefined", null)!!]!!
            val symbolsLayout = predefinedMethod.symbolLayout ?: CommonHardKeyboard.LAYOUTS[prefs.getString("method_ko_physical_symbols_hard_layout", null)!!]!!

            val combinationTable = predefinedMethod.combinationTable
            val virtualJamoTable = predefinedMethod.virtualJamoTable

            val converter =
                    when(predefinedMethod.hangulConverter) {
                        PredefinedHangulConverter.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable)
                    }

            val methodKo = HangulInputMethod(
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(symbolsLayout + predefinedMethod.hardLayout),
                    converter
            )

            physicalInputMethods += methodKo
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        physicalKeyboard = newConfig?.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES
        try {
            setInputView(currentMethod.initView(this))
        } catch(ex: IndexOutOfBoundsException) {
            currentMethodId = 0
            setInputView(currentMethod.initView(this))
        }
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
    }

    private fun reset() {
        currentMethodId = 0
    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {
        currentMethod.reset()

        val methods = if(physicalKeyboard) physicalInputMethods else softinputMethods

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window.attributes.token

        if(++currentMethodId >= methods.size) {
            currentMethodId = 0
            if(!inputAfterSwitch && switchBetweenApps) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)  switchToNextInputMethod(false)
                else imm.switchToNextInputMethod(token, false)
            }
        }

        inputAfterSwitch = false
        setInputView(currentMethod.initView(this))
    }

    @Subscribe fun onResetView(event: ResetViewEvent) {
        reloadPreferences()
        reset()
        setInputView(onCreateInputView())
    }

    @Subscribe fun onUpdateView(event: UpdateViewEvent) {
        currentMethod.updateView(this)?.let {
            setInputView(it)
        }
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
        when(event.state) {
            SoftKeyClickEvent.State.DOWN -> onSoftKeyDown(event)
            SoftKeyClickEvent.State.UP -> onSoftKeyUp(event)
        }
    }

    private fun onSoftKeyDown(event: SoftKeyClickEvent) {
        when(event.keyCode) {
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> {
                switchInputMethod(true)
                return
            }
        }
        inputAfterSwitch = true
        val result = currentMethod.onKeyPress(event.keyCode)
        // 입력 이벤트 처리가 되었으면 Release 이벤트 전송, 처리되지 않았으면 기본 처리를 수행.
        if(!result) when(event.keyCode) {
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
            else -> {
                sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, 0).toChar())
            }
        }
    }

    private fun onSoftKeyUp(event: SoftKeyClickEvent) {
        currentMethod.onKeyRelease(event.keyCode)
    }

    @Subscribe fun onSoftKeyLongClick(event: SoftKeyLongClickEvent) {
        if(!currentMethod.shift) currentMethod.onKeyPress(KeyEvent.KEYCODE_SHIFT_LEFT)
    }

    @Subscribe fun onSoftKeyFlick(event: SoftKeyFlickEvent) {
        when(event.direction) {
            SoftKeyFlickEvent.FlickDirection.UP -> {
                if(!currentMethod.shift) {
                    currentMethod.onKeyPress(KeyEvent.KEYCODE_SHIFT_LEFT)
                    currentMethod.onKeyRelease(KeyEvent.KEYCODE_SHIFT_RIGHT)
                }
            }
            SoftKeyFlickEvent.FlickDirection.DOWN -> {
                if(!currentMethod.alt) {
                    currentMethod.onKeyPress(KeyEvent.KEYCODE_ALT_LEFT)
                    currentMethod.onKeyRelease(KeyEvent.KEYCODE_ALT_LEFT)
                }
            }
        }
    }

    @Subscribe fun onCompose(event: ComposeEvent) {
        currentInputConnection?.setComposingText(event.composing, 1)
    }

    @Subscribe fun onCommitComposing(event: CommitComposingEvent) {
        currentInputConnection?.finishComposingText()
    }

    @Subscribe fun onCommitString(event: CommitStringEvent) {
        currentInputConnection?.finishComposingText()
        currentInputConnection?.commitText(event.string, 1)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if(keyCode == KeyEvent.KEYCODE_SPACE && event.isShiftPressed) {
            switchInputMethod(false)
            return true
        }
        return currentMethod.onKeyPress(keyCode) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return currentMethod.onKeyRelease(keyCode) || super.onKeyUp(keyCode, event)
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        outInsets?.contentTopInsets = outInsets?.visibleTopInsets
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        reloadPreferences()
        reset()
        setInputView(onCreateInputView())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    enum class PredefinedHangulConverter() {
        NONE, DUBEOL, DUBEOL_SINGLE_VOWEL, SEBEOL
    }

    data class PredefinedMethod(
            val softLayouts: List<Layout>,
            val hardLayout: CommonKeyboardLayout,
            val hangulConverter: PredefinedHangulConverter = PredefinedHangulConverter.NONE,
            val combinationTable: CombinationTable = CombinationTable(mapOf()),
            val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf()),
            val symbolLayout: CommonKeyboardLayout? = null
    )

    companion object {

        val SOFT_LAYOUT_12KEY = listOf(
                TwelveSoftLayout.LAYOUT_12KEY_4COLS
        )

        val SOFT_LAYOUT_UNIVERSAL = listOf(
                SoftLayout.LAYOUT_10COLS_MOBILE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM
        )

        val SOFT_LAYOUT_DVORAK = listOf(
                SoftLayout.LAYOUT_10COLS_DVORAK,
                SoftLayout.LAYOUT_10COLS_DVORAK_WITH_NUM
        )

        val SOFT_LAYOUT_SEBEOL_GONG = listOf(
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM
        )

        val SOFT_LAYOUT_SEBEOL_SHIN = listOf(
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM
        )

        val SOFT_LAYOUT_MINI_7COLS = listOf(
                MiniSoftLayout.LAYOUT_MINI_7COLS
        )

        val SOFT_LAYOUT_MINI_8COLS = listOf(
                MiniSoftLayout.LAYOUT_MINI_8COLS_GOOGLE
        )

        val PREDEFINED_METHODS = mapOf<String, PredefinedMethod>(
                "alphabet-qwerty" to PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, Alphabet.LAYOUT_QWERTY),
                "alphabet-dvorak" to PredefinedMethod(SOFT_LAYOUT_DVORAK, Alphabet.LAYOUT_DVORAK),
                "alphabet-7cols-wert" to PredefinedMethod(SOFT_LAYOUT_MINI_7COLS, Alphabet.LAYOUT_7COLS_WERT, symbolLayout = Symbols.LAYOUT_SYMBOLS_7COLS),

                "dubeol-standard" to PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD, PredefinedHangulConverter.DUBEOL, DubeolHangul.COMBINATION_DUBEOL_STANDARD),
                "sebeol-390" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_390, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391-strict" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_391),
                "sebeol-shin-original" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-shin-edit" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_EDIT, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-mini-shin" to PredefinedMethod(SOFT_LAYOUT_MINI_7COLS, ShinSebeolHangul.LAYOUT_MINI_SHIN_EXPERIMENTAL, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_MINI_SHIN_EXPERIMENTAL, symbolLayout = Symbols.LAYOUT_SYMBOLS_7COLS),
                "dubeol-google" to PredefinedMethod(SOFT_LAYOUT_MINI_8COLS, DubeolHangul.LAYOUT_DUBEOL_GOOGLE, PredefinedHangulConverter.DUBEOL_SINGLE_VOWEL, DubeolHangul.COMBINATION_DUBEOL_GOOGLE, symbolLayout = Symbols.LAYOUT_SYMBOLS_GOOGLE),
                "dubeol-cheonjiin" to PredefinedMethod(SOFT_LAYOUT_12KEY, TwelveDubeolHangul.LAYOUT_CHEONJIIN, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_CHEONJIIN),
                "dubeol-naratgeul" to PredefinedMethod(SOFT_LAYOUT_12KEY, TwelveDubeolHangul.LAYOUT_NARATGEUL, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_NARATGEUL)
        )
    }

}
