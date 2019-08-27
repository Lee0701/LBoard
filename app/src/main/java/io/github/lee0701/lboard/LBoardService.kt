package io.github.lee0701.lboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.support.v7.preference.PreferenceManager
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.old_event.*
import io.github.lee0701.lboard.hangul.*
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
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

class LBoardService: InputMethodService(), InputHistoryHolder, SharedPreferences.OnSharedPreferenceChangeListener {

    override val inputHistory: MutableMap<Int, MutableList<LBoardKeyEvent.Action>> = mutableMapOf()

    private val languageCycleTable = listOf("en", "ko")
    private var languageCycleIndex: Int = 0

    private val variationCycleTable = listOf("main", "symbols")
    private var variationCycleIndex: Int = 0

    private val inputMethods: MutableMap<String, InputMethod> = mutableMapOf()
    private val currentMethodId: String get() {
        val type = if(physicalKeyboardPresent) "physical" else "virtual"
        val language = languageCycleTable[languageCycleIndex]
        val variation = variationCycleTable[variationCycleIndex]
        return "method_%s_%s_%s".format(type, language, variation)
    }
    private val currentMethod: InputMethod? get() = inputMethods[currentMethodId]

    private var physicalKeyboardPresent: Boolean = false
    private var virtualKeyboardPresent: Boolean = false

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

    }

    private fun reloadPreferences() {
        inputMethods.values.forEach { it.destroy() }

        inputMethods.clear()

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
                    "method_virtual_en_main",
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout)
            )
            inputMethods += methodEn.methodId to methodEn

            val methodEnDirect = AlphabetInputMethod(
                    "method_virtual_en_direct",
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout)
            )
            inputMethods += methodEnDirect.methodId to methodEnDirect

            val methodEnSymbols = AlphabetInputMethod(
                    "method_virtual_en_symbols",
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            inputMethods += methodEnSymbols.methodId to methodEnSymbols
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
                    "method_virtual_ko_main",
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout),
                    converter,
                    timeout
            )
            inputMethods += methodKo.methodId to methodKo

            val methodKoSymbols = AlphabetInputMethod(
                    "method_virtual_ko_symbols",
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            inputMethods += methodKoSymbols.methodId to methodKoSymbols
        }

        run {
            val hardLayout = CommonHardKeyboard.LAYOUTS[pref.getString("method_en_physical_hard_layout", null)?: ""] ?: Alphabet.LAYOUT_QWERTY

            val methodEn = AlphabetInputMethod(
                    "method_physical_en_main",
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(hardLayout)
            )

            inputMethods += methodEn.methodId to methodEn
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
                    "method_physical_ko_main",
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(predefinedMethod.hardLayout),
                    converter
            )

            inputMethods += methodKo.methodId to methodKo
        }

        inputMethods.values.forEach { it.init() }

        EventBus.getDefault().post(PreferenceChangeEvent(pref))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        EventBus.getDefault().post(ConfigurationChangeEvent(newConfig))
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateInputView(): View? {
        EventBus.getDefault().post(InputViewInitEvent(this))
        return null
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        EventBus.getDefault().post(InputStartEvent())

        /*
        when(info.inputType and EditorInfo.TYPE_MASK_CLASS) {
            EditorInfo.TYPE_CLASS_TEXT -> when(info.inputType and EditorInfo.TYPE_MASK_VARIATION) {
                EditorInfo.TYPE_TEXT_VARIATION_PASSWORD,
                    EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                    EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD -> {
                    directInputMode = true
                }
                EditorInfo.TYPE_TEXT_VARIATION_URI,
                EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                    directInputMode = true
                }
                else -> {
                    directInputMode = false
                }
            }
        }

        currentMethod.reset()
        */
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        EventBus.getDefault().post(ConfigurationChangeEvent(resources.configuration))

    }

    override fun onFinishInput() {
        super.onFinishInput()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        EventBus.getDefault().post(HardKeyEvent(currentMethodId, keyCode,
                appendInputHistory(keyCode, LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.PRESS, System.currentTimeMillis())),
                event.isShiftPressed, event.isAltPressed))
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        EventBus.getDefault().post(HardKeyEvent(currentMethodId, keyCode,
                appendInputHistory(keyCode, LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.RELEASE, System.currentTimeMillis())),
                event.isShiftPressed, event.isAltPressed))
        inputHistory -= keyCode
        return true
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
        EventBus.getDefault().post(PreferenceChangeEvent(pref))
        if(listOf("common_soft_one_handed_mode").contains(key)) {
            EventBus.getDefault().post(UpdateOneHandedModeEvent(pref.getInt("common_soft_one_handed_mode", 0)))
        } else {
            reloadPreferences()
        }
    }

    override fun onDestroy() {
        inputMethods.values.forEach { it.destroy() }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
	fun onConfigurationChange(event: ConfigurationChangeEvent) {
        this.physicalKeyboardPresent = event.configuration.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES
    }

    @Subscribe
	fun onInputViewChange(event: InputViewChangeEvent) {
        if(event.methodId != currentMethodId) return
        println(event.inputView)
        if(event.inputView != null) {
            (event.inputView.parent as ViewGroup?)?.removeView(event.inputView)
            setInputView(event.inputView)
        }
        else setInputView(LinearLayout(this))
    }

    @Subscribe
	fun onInputProcessComplete(event: InputProcessCompleteEvent) {
        if(event.methodId != currentMethodId) return
        if(event.composingText?.commitPreviousText == true) currentInputConnection?.finishComposingText()
        if(event.sendRawInput) {
            val keyCode = event.keyEvent.keyCode
            val action = when(event.keyEvent.actions.last().type) {
                LBoardKeyEvent.ActionType.PRESS -> KeyEvent.ACTION_DOWN
                LBoardKeyEvent.ActionType.RELEASE -> KeyEvent.ACTION_UP
                else -> return
            }
            currentInputConnection?.sendKeyEvent(KeyEvent(action, keyCode))
        } else {
            event.composingText?.newComposingText?.let { currentInputConnection?.setComposingText(it, event.composingText.newCursorPosition) }
            event.composingText?.textToCommit?.let { currentInputConnection?.commitText(it, event.composingText.newCursorPosition) }
        }

    }

    @Subscribe(priority = 100)
    fun onKeyEvent(event: LBoardKeyEvent) {
        inputAfterSwitch = true

        if(isSystemKey(event.keyCode)) {
            EventBus.getDefault().cancelEventDelivery(event)
            sendDownUpKeyEvents(event.keyCode)
        }

        if(event.keyCode == KeyEvent.KEYCODE_LANGUAGE_SWITCH) {
            switchInputMethod(event is SoftKeyEvent)
            EventBus.getDefault().cancelEventDelivery(event)
        }

        if(event is HardKeyEvent) {
            if(event.keyCode == KeyEvent.KEYCODE_SPACE && event.shiftPressed) {
                switchInputMethod(false)
                EventBus.getDefault().cancelEventDelivery(event)
            }
        }

    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window.attributes.token

        if(++languageCycleIndex >= languageCycleTable.size) {
            languageCycleIndex = 0
            if(!inputAfterSwitch && switchBetweenApps) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToNextInputMethod(false)
                else imm.switchToNextInputMethod(token, false)
            }
        }

        inputAfterSwitch = false
        onCreateInputView()
        EventBus.getDefault().post(InputStartEvent())
    }

    private fun isSystemKey(keyCode: Int): Boolean = keyCode in 0 .. 6 || keyCode in 24 .. 28 || keyCode in 79 .. 85

    private fun showInputMethodPicker() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showInputMethodPicker()
    }

    private fun showSettingsApp() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /*
    @Subscribe
	fun onResetView(event: ResetViewEvent) {
        reloadPreferences()
        reset()
        setInputView(onCreateInputView())
    }

    @Subscribe
	fun onUpdateOneHandedMode(event: UpdateOneHandedModeEvent) {
        allInputMethods.forEach {
            if(it is CommonInputMethod) it.softKeyboard.updateOneHandedMode(event.oneHandedMode)
        }
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putInt("common_soft_one_handed_mode", event.oneHandedMode)
        editor.apply()
    }

    @Subscribe
	fun onSetSymbolMode(event: SetSymbolModeEvent) {
        setSymbolMode(event.symbolMode)
    }

    @Subscribe
	fun onKeyPress(event: KeyPressEvent) {
        this.onKeyDown(event.keyCode, KeyEvent(KeyEvent.ACTION_DOWN, event.keyCode))
    }

    @Subscribe
	fun onKeyRelease(event: KeyReleaseEvent) {
        this.onKeyUp(event.keyCode, KeyEvent(KeyEvent.ACTION_UP, event.keyCode))
    }

    @Subscribe
	fun onSoftKeyClick(event: SoftKeyClickEvent) {
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

    @Subscribe
	fun onSoftKeyLongClick(event: SoftKeyLongClickEvent) {
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

    @Subscribe
	fun onSoftKeyFlick(event: SoftKeyFlickEvent) {
        if(listOf(KeyEvent.KEYCODE_SYM, KeyEvent.KEYCODE_LANGUAGE_SWITCH, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DEL)
                        .contains(event.keyCode)) return
        currentMethod.onKeyFlick(event.keyCode, event.direction)
    }

    @Subscribe
	fun onCompose(event: ComposeEvent) {
        currentInputConnection?.setComposingText(event.composing, 1)
    }

    @Subscribe
	fun onCommitComposing(event: CommitComposingEvent) {
        currentInputConnection?.finishComposingText()
    }

    @Subscribe
	fun onCommitString(event: CommitStringEvent) {
        currentInputConnection?.finishComposingText()
        currentInputConnection?.commitText(event.string, 1)
    }
    */

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
