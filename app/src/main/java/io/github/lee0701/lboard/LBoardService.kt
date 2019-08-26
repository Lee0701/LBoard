package io.github.lee0701.lboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Vibrator
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
import io.github.lee0701.lboard.hardkeyboard.ExtendedCode
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.layouts.soft.*
import io.github.lee0701.lboard.layouts.symbols.Symbols
import io.github.lee0701.lboard.settings.SettingsActivity
import io.github.lee0701.lboard.softkeyboard.*
import io.github.lee0701.lboard.softkeyboard.EmptySoftKeyboard
import io.github.lee0701.lboard.softkeyboard.themes.BasicSoftKeyboardTheme
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LBoardService: InputMethodService(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val softInputMethods: MutableList<InputMethod> = mutableListOf()
    private val symbolInputMethods: MutableList<InputMethod> = mutableListOf()
    private val directInputMethods: MutableList<InputMethod> = mutableListOf()
    private val physicalInputMethods: MutableList<InputMethod> = mutableListOf()

    private val allInputMethods: List<InputMethod>
        get() = softInputMethods + symbolInputMethods + directInputMethods + physicalInputMethods

    private var symbolKeyboardMode: Boolean = false
    private var directInputMode: Boolean = false
    private var physicalKeyboardMode: Boolean = false
    private var currentMethodId: Int = 0
    private val currentMethod: InputMethod get() =
        if(symbolKeyboardMode) symbolInputMethods[currentMethodId]
        else if(physicalKeyboardMode) physicalInputMethods[currentMethodId]
        else if(directInputMode) directInputMethods[currentMethodId]
        else softInputMethods[currentMethodId]

    private var switchBetweenApps: Boolean = true

    private var inputAfterSwitch: Boolean = false

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_common, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_en, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_ko, true)

        reloadPreferences()

        physicalKeyboardMode = resources.configuration.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES

    }

    private fun reloadPreferences() {
        softInputMethods.clear()
        directInputMethods.clear()
        symbolInputMethods.clear()
        physicalInputMethods.clear()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        switchBetweenApps = pref.getBoolean("common_soft_switch_between_methods", switchBetweenApps)

        val theme = BasicSoftKeyboard.THEMES[pref.getString("common_soft_theme", null) ?: ""] ?: BasicSoftKeyboardTheme.WHITE

        run {
            val predefinedMethod = PREDEFINED_METHODS[pref.getString("method_en_predefined", null) ?: ""] ?: PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, Alphabet.LAYOUT_QWERTY)

            val symbolsSoftLayout = BasicSoftKeyboard.LAYOUTS[pref.getString("method_en_symbols_soft_layout", null)?: ""] ?: SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM
            val symbolsHardLayout = CommonHardKeyboard.LAYOUTS[pref.getString("method_en_symbols_hard_layout", null)?: ""] ?: Symbols.LAYOUT_SYMBOLS_B
            val layer10SymbolsHardLayout = CommonKeyboardLayout(10, symbolsHardLayout[0] ?: CommonKeyboardLayout.LayoutLayer(mapOf()))

            val moreKeysLayouts = (pref.getStringSet("method_en_more_keys_layouts", null) ?: setOf())
                    .map { name -> CommonHardKeyboard.LAYOUTS[name] }.filterNotNull()
            val moreKeysLayout = if(moreKeysLayouts.isNotEmpty()) moreKeysLayouts.reduceRight { layout, acc -> layout + acc } else CommonKeyboardLayout()

            val softLayout = BasicSoftKeyboard.LAYOUTS[pref.getString("method_en_soft_layout", null) ?: ""] ?: SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM
            val hardLayout = layer10SymbolsHardLayout + moreKeysLayout + predefinedMethod.hardLayout

            val methodEn = WordComposingInputMethod(
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout)
            )
            softInputMethods += methodEn

            val methodEnDirect = AlphabetInputMethod(
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout)
            )
            directInputMethods += methodEnDirect

            val methodEnSymbols = AlphabetInputMethod(
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            symbolInputMethods += methodEnSymbols
        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[pref.getString("method_ko_predefined", null)?: ""] ?: PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD)

            val timeout = pref.getInt("method_ko_timeout", 0)

            val symbolsSoftLayout = BasicSoftKeyboard.LAYOUTS[pref.getString("method_ko_symbols_soft_layout", null)?: ""] ?: SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM
            val symbolsHardLayout = CommonHardKeyboard.LAYOUTS[pref.getString("method_ko_symbols_hard_layout", null)?: ""] ?: Symbols.LAYOUT_SYMBOLS_B
            val layer10SymbolsHardLayout = CommonKeyboardLayout(10, symbolsHardLayout[0] ?: CommonKeyboardLayout.LayoutLayer(mapOf()))

            val moreKeysLayouts = (pref.getStringSet("method_ko_more_keys_layouts", null) ?: setOf())
                    .map { name -> CommonHardKeyboard.LAYOUTS[name] }.filterNotNull()
            val moreKeysLayout = if(moreKeysLayouts.isNotEmpty()) moreKeysLayouts.reduceRight { layout, acc -> layout + acc } else CommonKeyboardLayout()

            val softLayout = BasicSoftKeyboard.LAYOUTS[pref.getString("method_ko_soft_layout", null)?: ""] ?: SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM
            val hardLayout = layer10SymbolsHardLayout + moreKeysLayout + predefinedMethod.hardLayout

            val combinationTable = predefinedMethod.combinationTable
            val virtualJamoTable = predefinedMethod.virtualJamoTable

            val converter =
                    when(predefinedMethod.hangulConverter) {
                        PredefinedHangulConverter.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable)
                        PredefinedHangulConverter.DUBEOL_SINGLE_VOWEL -> SingleVowelDubeolHangulComposer(combinationTable, virtualJamoTable)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable)
                    }

            val methodKo = HangulInputMethod(
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout),
                    converter,
                    timeout
            )
            softInputMethods += methodKo

            val methodKoSymbols = AlphabetInputMethod(
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            symbolInputMethods += methodKoSymbols
        }

        run {
            val hardLayout = CommonHardKeyboard.LAYOUTS[pref.getString("method_en_physical_hard_layout", null)?: ""] ?: Alphabet.LAYOUT_QWERTY

            val methodEn = AlphabetInputMethod(
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(hardLayout)
            )

            physicalInputMethods += methodEn
        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[pref.getString("method_ko_physical_predefined", null)?: ""] ?: PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD)

            val combinationTable = predefinedMethod.combinationTable
            val virtualJamoTable = predefinedMethod.virtualJamoTable

            val converter =
                    when(predefinedMethod.hangulConverter) {
                        PredefinedHangulConverter.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable)
                    }

            val methodKo = HangulInputMethod(
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(predefinedMethod.hardLayout),
                    converter
            )

            physicalInputMethods += methodKo
        }

        allInputMethods.forEach { it.setPreferences(pref) }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        physicalKeyboardMode = newConfig?.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES
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
        symbolKeyboardMode = false
    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {
        currentMethod.reset()
        symbolKeyboardMode = false

        val methods = if(physicalKeyboardMode) physicalInputMethods else softInputMethods

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window.attributes.token

        if(++currentMethodId >= methods.size) {
            currentMethodId = 0
            if(!inputAfterSwitch && switchBetweenApps) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToNextInputMethod(false)
                else imm.switchToNextInputMethod(token, false)
            }
        }

        inputAfterSwitch = false
        setInputView(currentMethod.initView(this))
    }

    private fun showInputMethodPicker() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
    }

    private fun showSettingsApp() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setSymbolMode(symbolMode: Boolean) {
        if(symbolKeyboardMode == symbolMode) return
        currentMethod.reset()
        symbolKeyboardMode = symbolMode
        try {
            setInputView(currentMethod.initView(this))
        } catch(ex: IndexOutOfBoundsException) {
            currentMethodId = 0
            setInputView(currentMethod.initView(this))
        }
    }

    @Subscribe fun onResetView(event: ResetViewEvent) {
        reloadPreferences()
        reset()
        setInputView(onCreateInputView())
    }

    @Subscribe(threadMode = ThreadMode.MAIN) fun onUpdateView(event: UpdateViewEvent) {
        currentMethod.updateView(this)
    }

    @Subscribe fun onUpdateOneHandedMode(event: UpdateOneHandedModeEvent) {
        allInputMethods.forEach {
            if(it is CommonInputMethod) it.softKeyboard.updateOneHandedMode(event.oneHandedMode)
        }
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putInt("common_soft_one_handed_mode", event.oneHandedMode)
        editor.apply()
    }

    @Subscribe fun onSetSymbolMode(event: SetSymbolModeEvent) {
        setSymbolMode(event.symbolMode)
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
                switchInputMethod(this.switchBetweenApps)
                return
            }
            KeyEvent.KEYCODE_SYM -> {
                setSymbolMode(!symbolKeyboardMode)
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
                sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD).get(event.keyCode, 0).toChar())
            }
        }
    }

    private fun onSoftKeyUp(event: SoftKeyClickEvent) {
        currentMethod.onKeyRelease(event.keyCode)
    }

    @Subscribe fun onSoftKeyLongClick(event: SoftKeyLongClickEvent) {
        when(event.keyCode) {
            KeyEvent.KEYCODE_SYM -> {}
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> showInputMethodPicker()
            KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_PERIOD -> showSettingsApp()
            KeyEvent.KEYCODE_SPACE -> {}
            KeyEvent.KEYCODE_ENTER -> {}
            KeyEvent.KEYCODE_DEL -> {}
            else -> {
                currentMethod.onKeyLongPress(event.keyCode)
                return
            }
        }
    }

    @Subscribe fun onSoftKeyFlick(event: SoftKeyFlickEvent) {
        if(listOf(KeyEvent.KEYCODE_SYM, KeyEvent.KEYCODE_LANGUAGE_SWITCH, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DEL)
                        .contains(event.keyCode)) return
        currentMethod.onKeyFlick(event.keyCode, event.direction)
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
        when(keyCode) {
            KeyEvent.KEYCODE_SYM -> {
                setSymbolMode(!symbolKeyboardMode)
                return true
            }
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

    override fun onSharedPreferenceChanged(pref: SharedPreferences, key: String) {
        if(listOf("common_soft_one_handed_mode").contains(key)) {
            EventBus.getDefault().post(UpdateOneHandedModeEvent(pref.getInt("common_soft_one_handed_mode", 0)))
        } else {
            reloadPreferences()
            reset()
            setInputView(onCreateInputView())
        }
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
            val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf())
    )

    companion object {

        val SOFT_LAYOUT_MODE_MOBILE = listOf(
                TwelveSoftLayout.LAYOUT_12KEY_4COLS,
                MiniSoftLayout.LAYOUT_MINI_7COLS,
                MiniSoftLayout.LAYOUT_MINI_8COLS_GOOGLE,
                SoftLayout.LAYOUT_10COLS_MOBILE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE_NUM,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                SoftLayout.LAYOUT_10COLS_DVORAK,
                SoftLayout.LAYOUT_10COLS_DVORAK_WITH_NUM
        )

        val SOFT_LAYOUT_MODE_TABLET = listOf(
                TabletSoftLayout.LAYOUT_11COLS_TABLET,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM
        )

        val SOFT_LAYOUT_MODE_FULL = listOf(
                FullSoftLayout.LAYOUT_FULL
        )

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
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL
        )

        val SOFT_LAYOUT_DVORAK = listOf(
                SoftLayout.LAYOUT_10COLS_DVORAK,
                SoftLayout.LAYOUT_10COLS_DVORAK_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL
        )

        val SOFT_LAYOUT_COLEMAK = listOf(
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE_NUM,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL
        )

        val SOFT_LAYOUT_SEBEOL_GONG = listOf(
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL
        )

        val SOFT_LAYOUT_SEBEOL_SHIN = listOf(
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL
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
                "alphabet-colemak" to PredefinedMethod(SOFT_LAYOUT_COLEMAK, Alphabet.LAYOUT_COLEMAK),
                "alphabet-7cols-wert" to PredefinedMethod(SOFT_LAYOUT_MINI_7COLS, Alphabet.LAYOUT_7COLS_WERT),

                "dubeol-standard" to PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD, PredefinedHangulConverter.DUBEOL, DubeolHangul.COMBINATION_DUBEOL_STANDARD),
                "sebeol-390" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_390, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391-strict" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391_STRICT, PredefinedHangulConverter.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_391_STRICT),
                "sebeol-shin-original" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-shin-edit" to PredefinedMethod(SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_EDIT, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-mini-shin" to PredefinedMethod(SOFT_LAYOUT_MINI_7COLS, ShinSebeolHangul.LAYOUT_MINI_SHIN_EXPERIMENTAL, PredefinedHangulConverter.SEBEOL, ShinSebeolHangul.COMBINATION_MINI_SHIN_EXPERIMENTAL),
                "dubeol-google" to PredefinedMethod(SOFT_LAYOUT_MINI_8COLS, DubeolHangul.LAYOUT_DUBEOL_GOOGLE, PredefinedHangulConverter.DUBEOL_SINGLE_VOWEL, DubeolHangul.COMBINATION_DUBEOL_GOOGLE),
                "dubeol-cheonjiin" to PredefinedMethod(SOFT_LAYOUT_12KEY, TwelveDubeolHangul.LAYOUT_CHEONJIIN, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_CHEONJIIN),
                "dubeol-naratgeul" to PredefinedMethod(SOFT_LAYOUT_12KEY, TwelveDubeolHangul.LAYOUT_NARATGEUL, PredefinedHangulConverter.DUBEOL, TwelveDubeolHangul.COMBINATION_NARATGEUL)
        )

        fun getMode(modeName: String): List<Layout> {
            return when(modeName) {
                "mobile" -> SOFT_LAYOUT_MODE_MOBILE
                "tablet" -> SOFT_LAYOUT_MODE_TABLET
                "full" -> SOFT_LAYOUT_MODE_FULL
                else -> listOf()
            }
        }

    }

}
