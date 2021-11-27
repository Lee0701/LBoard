package io.github.lee0701.lboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.preference.PreferenceManager
import io.github.lee0701.lboard.candidates.RecyclerCandidateViewManager
import io.github.lee0701.lboard.candidates.CandidateViewManager
import io.github.lee0701.lboard.dictionary.CompoundDictionary
import io.github.lee0701.lboard.dictionary.FlatTrieDictionary
import io.github.lee0701.lboard.dictionary.WeightedDictionary
import io.github.lee0701.lboard.dictionary.WritableTrieDictionary
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hangul.*
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.inputmethod.*
import io.github.lee0701.lboard.inputmethod.ambiguous.HangulSyllableFrequencyScorer
import io.github.lee0701.lboard.inputmethod.ambiguous.KoreanDictionaryCandidateGenerator
import io.github.lee0701.lboard.inputmethod.predefined.PredefinedMethod
import io.github.lee0701.lboard.inputmethod.predefined.PredefinedMethod.Companion.PREDEFINED_METHODS
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.layouts.soft.*
import io.github.lee0701.lboard.layouts.symbols.Symbols
import io.github.lee0701.lboard.prediction.DictionaryPredictor
import io.github.lee0701.lboard.settings.SettingsActivity
import io.github.lee0701.lboard.softkeyboard.*
import io.github.lee0701.lboard.softkeyboard.EmptySoftKeyboard
import io.github.lee0701.lboard.softkeyboard.themes.BasicSoftKeyboardTheme
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class LBoardService: InputMethodService(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var showCandidateView: Boolean = true
    private var candidateViewManager: CandidateViewManager? = null

    val inputHistory: MutableMap<Int, MutableList<LBoardKeyEvent.Action>> = mutableMapOf()

    private val languageCycleTable = listOf("en", "ko")
    private var languageCycleIndex: Int = 0
    private var contextLanguageIndex: Int? = null

    private val variationCycleTable = listOf(InputMethodInfo.Type.MAIN, InputMethodInfo.Type.SYMBOLS)
    private var variationCycleIndex: Int = 0

    private var directInputMode: Boolean = false

    private val inputMethods: MutableMap<InputMethodInfo, InputMethod> = mutableMapOf()
    private lateinit var currentMethod: InputMethod

    private var orientation: Orientation = Orientation.PORTRAIT
    private var physicalKeyboardPresent: Boolean = false
    private var virtualKeyboardPresent: Boolean = false

    private var switchBetweenApps: Boolean = true
    private var automaticDarkMode: Boolean = true

    private var darkMode: Boolean? = null
    private var inputAfterSwitch: Boolean = false

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_common, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_en, true)
        PreferenceManager.setDefaultValues(this, R.xml.lboard_pref_method_ko, true)

        init()

    }

    private fun reloadPreferences() {
        candidateViewManager?.destroy()
        inputMethods.values.forEach { it.destroy() }
        inputMethods.clear()

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        switchBetweenApps = pref.getBoolean("common_soft_switch_between_methods", switchBetweenApps)
        automaticDarkMode = pref.getBoolean("common_soft_theme_automatic_dark_mode", automaticDarkMode)

        var theme = BasicSoftKeyboard.THEMES[pref.getString("common_soft_theme", null) ?: ""] ?: BasicSoftKeyboardTheme.WHITE

        if(automaticDarkMode && darkMode != null) {
            theme = (if(darkMode == true) BasicSoftKeyboard.DARK_THEMES else BasicSoftKeyboard.LIGHT_THEMES)[theme] ?: theme
        }

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

            val methodEn = when(predefinedMethod.methodType) {
                PredefinedMethod.Type.PREDICTIVE -> {
                    val userDictFile = File(filesDir, "userdict.en.txt")
                    val dictionary = CompoundDictionary(listOf(
                            FlatTrieDictionary(assets.open("dict/en/dict.bin").readBytes()),
                            WeightedDictionary(WritableTrieDictionary(userDictFile), 1.3f)
                    ))
                    PredictiveInputMethod(
                            InputMethodInfo(language = "en", device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = false, predictive = true),
                            BasicSoftKeyboard(softLayout.clone(), theme),
                            CommonHardKeyboard(hardLayout),
                            DictionaryPredictor(dictionary, predefinedMethod.hardLayout[0]!!.layout.mapValues { it.value.normal + it.value.shift })
                    )
                }
                else -> WordComposingInputMethod(
                        InputMethodInfo(language = "en", device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = false),
                        BasicSoftKeyboard(softLayout.clone(), theme),
                        CommonHardKeyboard(hardLayout)
                )
            }
            inputMethods += methodEn.info to methodEn

            val methodEnDirect = AlphabetInputMethod(
                    InputMethodInfo(device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = true),
                    BasicSoftKeyboard(softLayout.clone(), theme),
                    CommonHardKeyboard(hardLayout)
            )
            inputMethods += methodEnDirect.info to methodEnDirect

            val methodEnSymbols = AlphabetInputMethod(
                    InputMethodInfo(language = "en", type = InputMethodInfo.Type.SYMBOLS),
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            inputMethods += methodEnSymbols.info to methodEnSymbols
        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[pref.getString("method_ko_predefined", null)?: ""] ?: PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD)

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
                    when(predefinedMethod.methodType) {
                        PredefinedMethod.Type.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable, true)
                        PredefinedMethod.Type.DUBEOL_SINGLE_VOWEL -> SingleVowelDubeolHangulComposer(combinationTable, virtualJamoTable, true)
                        PredefinedMethod.Type.DUBEOL_AMBIGUOUS -> DubeolHangulComposer(combinationTable, virtualJamoTable, false)
                        PredefinedMethod.Type.SEBEOL_AMBIGUOUS -> SebeolHangulComposer(combinationTable, virtualJamoTable, false)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable, true)
                    }

            val methodKo = when(predefinedMethod.methodType) {
                PredefinedMethod.Type.DUBEOL_AMBIGUOUS,
                PredefinedMethod.Type.SEBEOL_AMBIGUOUS -> {
                    val userDictFile = File(filesDir, "userdict.ko.txt")
                    val dictionary = CompoundDictionary(listOf(
                            FlatTrieDictionary(assets.open("dict/ko/dict.bin").readBytes()),
                            WeightedDictionary(WritableTrieDictionary(userDictFile), 1.3f)
                    ))
                    AmbiguousHangulInputMethod(
                            InputMethodInfo(language = "ko", device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = false, predictive = true),
                            BasicSoftKeyboard(softLayout.clone(), theme),
                            CommonHardKeyboard(hardLayout),
                            converter,
                            HangulSyllableFrequencyScorer(),
                            KoreanDictionaryCandidateGenerator(dictionary))
                }
                else -> HangulInputMethod(
                        InputMethodInfo(language = "ko", device = InputMethodInfo.Device.VIRTUAL, type = InputMethodInfo.Type.MAIN, direct = false),
                        BasicSoftKeyboard(softLayout.clone(), theme),
                        CommonHardKeyboard(hardLayout),
                        converter)
            }
            inputMethods += methodKo.info to methodKo

            val methodKoSymbols = AlphabetInputMethod(
                    InputMethodInfo(language = "ko", type = InputMethodInfo.Type.SYMBOLS),
                    BasicSoftKeyboard(symbolsSoftLayout.clone(), theme),
                    CommonHardKeyboard(symbolsHardLayout)
            )
            inputMethods += methodKoSymbols.info to methodKoSymbols
        }

        run {
            val hardLayout = CommonHardKeyboard.LAYOUTS[pref.getString("method_en_physical_hard_layout", null)?: ""] ?: Alphabet.LAYOUT_QWERTY

            val methodEn = AlphabetInputMethod(
                    InputMethodInfo(language = "en", device = InputMethodInfo.Device.PHYSICAL, type = InputMethodInfo.Type.MAIN, direct = false),
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(hardLayout)
            )
            inputMethods += methodEn.info to methodEn

            val methodEnDirect = AlphabetInputMethod(
                    InputMethodInfo(device = InputMethodInfo.Device.PHYSICAL, type = InputMethodInfo.Type.MAIN, direct = true),
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(hardLayout)
            )
            inputMethods += methodEnDirect.info to methodEnDirect

        }

        run {
            val predefinedMethod = PREDEFINED_METHODS[pref.getString("method_ko_physical_predefined", null)?: ""] ?: PredefinedMethod(SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD)

            val combinationTable = predefinedMethod.combinationTable
            val virtualJamoTable = predefinedMethod.virtualJamoTable

            val converter =
                    when(predefinedMethod.methodType) {
                        PredefinedMethod.Type.DUBEOL -> DubeolHangulComposer(combinationTable, virtualJamoTable, true)
                        else -> SebeolHangulComposer(combinationTable, virtualJamoTable, true)
                    }

            val methodKo = HangulInputMethod(
                    InputMethodInfo(language = "ko", device = InputMethodInfo.Device.PHYSICAL, type = InputMethodInfo.Type.MAIN, direct = false),
                    EmptySoftKeyboard(),
                    CommonHardKeyboard(predefinedMethod.hardLayout),
                    converter
            )

            inputMethods += methodKo.info to methodKo
        }

        inputMethods.values.forEach { it.init() }
        EventBus.getDefault().post(PreferenceChangeEvent(pref))

        showCandidateView = inputMethods.any { it.value.info.predictive == true }
        candidateViewManager = RecyclerCandidateViewManager(theme.background, theme.keyTheme[null]?.textColor ?: Color.BLACK)
        candidateViewManager?.init()

        updateCurrentMethod()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        EventBus.getDefault().post(ConfigurationChangeEvent(newConfig))
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateInputView(): View? {
        EventBus.getDefault().post(InputViewInitEvent(this))
        return null
    }

    override fun onStartInputView(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)

        directInputMode = false
        contextLanguageIndex = null

        when(attribute.inputType and EditorInfo.TYPE_MASK_CLASS) {
            EditorInfo.TYPE_CLASS_TEXT -> when(attribute.inputType and EditorInfo.TYPE_MASK_VARIATION) {
                EditorInfo.TYPE_TEXT_VARIATION_PASSWORD,
                EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD -> {
                    directInputMode = true
                }
                EditorInfo.TYPE_TEXT_VARIATION_URI,
                EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                    contextLanguageIndex = 0
                }
            }
        }

        EventBus.getDefault().post(InputStartEvent())
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
    }

    override fun onStartInput(attribute: EditorInfo, restarting: Boolean) {
        super.onStartInput(attribute, restarting)

        EventBus.getDefault().post(ConfigurationChangeEvent(resources.configuration))
    }

    override fun onFinishInput() {
        super.onFinishInput()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if(isSystemKey(keyCode)) return super.onKeyDown(keyCode, event)
        EventBus.getDefault().post(LBoardKeyEvent(currentMethod.info, keyCode, LBoardKeyEvent.Source.PHYSICAL_KEYBOARD,
                appendInputHistory(keyCode, LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.PRESS, keyCode, System.currentTimeMillis())),
                event.isShiftPressed, event.isAltPressed))
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if(isSystemKey(keyCode)) return super.onKeyUp(keyCode, event)
        EventBus.getDefault().post(LBoardKeyEvent(currentMethod.info, keyCode, LBoardKeyEvent.Source.PHYSICAL_KEYBOARD,
                appendInputHistory(keyCode, LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.RELEASE, keyCode, System.currentTimeMillis())),
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
            EventBus.getDefault().post(OneHandedModeUpdateEvent(pref.getInt("common_soft_one_handed_mode", 0)))
        } else {
            reloadPreferences()
            onCreateInputView()
        }
    }

    override fun onDestroy() {
        this.destroy()
        INSTANCE = null
        super.onDestroy()
    }

    @Subscribe(priority = 100)
    fun onInputStart(event: InputStartEvent) {
        updateCurrentMethod()
        onCreateInputView()
    }

    @Subscribe
	fun onConfigurationChange(event: ConfigurationChangeEvent) {
        this.physicalKeyboardPresent = event.configuration.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_YES

        val orientation = when(event.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Orientation.PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> Orientation.LANDSCAPE
            else -> null
        }
        if(orientation != null && orientation != this.orientation) {
            this.orientation = orientation
            reloadPreferences()
            onCreateInputView()
        }

        val darkMode = when(event.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> null
        }
        if(darkMode != this.darkMode) {
            this.darkMode = darkMode
            reloadPreferences()
            onCreateInputView()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
	fun onInputViewChange(event: InputViewChangeEvent) {
        if(!event.methodInfo.match(currentMethod.info)) return
        val topView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        if(showCandidateView) {
            candidateViewManager?.let {
                val candidateView = it.initView(this)
                if(candidateView != null && candidateView.parent != null) (candidateView.parent as ViewGroup).removeView(candidateView)
                topView.addView(candidateView)
            }
        }

        if(event.inputView != null) {
            (event.inputView.parent as ViewGroup?)?.removeView(event.inputView)
            topView.addView(event.inputView)
        }
        setInputView(topView)
    }

    @Subscribe
    fun onOneHandedModeUpdate(event: OneHandedModeUpdateEvent) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putInt("common_soft_one_handed_mode", event.oneHandedMode)
        editor.apply()
    }

    @Subscribe
    fun onInputReset(event: InputResetEvent) {
        if(!event.methodInfo.match(currentMethod.info)) return
        currentInputConnection?.finishComposingText()
    }

    @Subscribe
	fun onInputProcessComplete(event: InputProcessCompleteEvent) {
        if(!event.methodInfo.match(currentMethod.info)) return
        if(event.composingText?.commitPreviousText == true) currentInputConnection?.finishComposingText()
        if(event.commitDefaultChar) {
            val keyCode = event.keyEvent.lastKeyCode
            val shift = event.keyEvent.shiftPressed
            val alt = event.keyEvent.altPressed
            val metaState = if(shift) KeyEvent.META_SHIFT_ON else 0 or if(alt) KeyEvent.META_ALT_ON else 0
            val char = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD).get(keyCode, metaState)
            if(char > 0) currentInputConnection?.commitText(char.toChar().toString(), 1)
        }
        if(event.sendRawInput) {
            val keyCode = event.keyEvent.lastKeyCode
            val shift = event.keyEvent.shiftPressed
            val alt = event.keyEvent.altPressed
            val metaState = if(shift) KeyEvent.META_SHIFT_ON else 0 or if(alt) KeyEvent.META_ALT_ON else 0
            val time = event.keyEvent.actions.last().time
            val action = when(event.keyEvent.actions.last().type) {
                LBoardKeyEvent.ActionType.PRESS, LBoardKeyEvent.ActionType.REPEAT -> KeyEvent.ACTION_DOWN
                LBoardKeyEvent.ActionType.RELEASE -> KeyEvent.ACTION_UP
                else -> return
            }
            val repeat = event.keyEvent.actions.count { it.type == LBoardKeyEvent.ActionType.REPEAT }
            val imeAction = currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
            if(keyCode == KeyEvent.KEYCODE_ENTER && currentInputConnection?.getTextBeforeCursor(1, 0) == " ")
                currentInputConnection?.deleteSurroundingText(1, 0)
            if(keyCode == KeyEvent.KEYCODE_ENTER && listOf(EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_GO).contains(imeAction))
                sendDefaultEditorAction(true)
            else currentInputConnection?.sendKeyEvent(KeyEvent(time, time, action, keyCode, repeat, metaState))
        } else {
            event.composingText?.newComposingText?.let { currentInputConnection?.setComposingText(it, event.composingText.newCursorPosition) }
            event.composingText?.textToCommit?.let {
                val space = listOf(0x2c, 0x2e, 0x3f, 0x21).contains(it.firstOrNull()?.code ?: 0) && currentInputConnection?.getTextBeforeCursor(1, 0) == " "
                if(space) currentInputConnection?.deleteSurroundingText(1, 0)
                currentInputConnection?.commitText(it, event.composingText.newCursorPosition)
                if(space) currentInputConnection?.commitText(" ", 1)
            }
        }

    }

    @Subscribe
    fun onKeyPress(event: KeyPressEvent) {
        val actions = appendInputHistory(event.keyCode, LBoardKeyEvent.Action(event.type, event.keyCode, System.currentTimeMillis()))
        EventBus.getDefault().post(LBoardKeyEvent(currentMethod.info, event.keyCode, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD,
                actions, event.shift, event.alt))
        if(event.type == LBoardKeyEvent.ActionType.RELEASE) removeInputHistory(event.keyCode)
    }

    @Subscribe
    fun onMoreKeySelect(event: MoreKeySelectEvent) {
        val actions = appendInputHistory(event.originalKeyCode,
                LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.SELECT_MORE_KEYS, event.newKeyCode, System.currentTimeMillis()))
        EventBus.getDefault().post(LBoardKeyEvent(currentMethod.info, event.originalKeyCode, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD,
                actions, false, false))
        removeInputHistory(event.originalKeyCode)
    }

    @Subscribe(priority = 100)
    fun onKeyEvent(event: LBoardKeyEvent) {
        if(isSystemKey(event.originalKeyCode)) {
            EventBus.getDefault().cancelEventDelivery(event)
            sendDownUpKeyEvents(event.originalKeyCode)
            return
        }
        if(event.source == LBoardKeyEvent.Source.VIRTUAL_KEYBOARD) {
            when(event.actions.last().type) {
                LBoardKeyEvent.ActionType.FLICK_UP, LBoardKeyEvent.ActionType.FLICK_DOWN,
                LBoardKeyEvent.ActionType.FLICK_LEFT, LBoardKeyEvent.ActionType.FLICK_RIGHT -> {
                    if(listOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_DEL,
                                    KeyEvent.KEYCODE_SYM, KeyEvent.KEYCODE_LANGUAGE_SWITCH,
                                    KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT)
                                    .contains(event.originalKeyCode)) {
                        EventBus.getDefault().cancelEventDelivery(event)
                        return
                    }
                }
                else -> {}
            }

            if(event.actions.last().type == LBoardKeyEvent.ActionType.LONG_PRESS) {
                if(event.originalKeyCode == KeyEvent.KEYCODE_LANGUAGE_SWITCH) {
                    showInputMethodPicker()
                    return
                }
                if(event.originalKeyCode == KeyEvent.KEYCODE_COMMA || event.originalKeyCode == KeyEvent.KEYCODE_PERIOD) {
                    showSettingsApp()
                    return
                }
            }
            if(event.actions.find { it.type == LBoardKeyEvent.ActionType.LONG_PRESS } != null) {
                if(listOf(KeyEvent.KEYCODE_LANGUAGE_SWITCH, KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_PERIOD).contains(event.originalKeyCode)) {
                    EventBus.getDefault().cancelEventDelivery(event)
                    return
                }
            }

            if(event.actions.last().type == LBoardKeyEvent.ActionType.RELEASE) {
                if(event.originalKeyCode == KeyEvent.KEYCODE_LANGUAGE_SWITCH) {
                    switchInputMethod(switchBetweenApps)
                    EventBus.getDefault().cancelEventDelivery(event)
                    return
                }
                if(event.originalKeyCode == KeyEvent.KEYCODE_SYM) {
                    switchVariation()
                    EventBus.getDefault().cancelEventDelivery(event)
                    return
                }
            }

        }

        if(event.source == LBoardKeyEvent.Source.PHYSICAL_KEYBOARD) {
            if(event.actions.last().type == LBoardKeyEvent.ActionType.PRESS) {
                if(event.originalKeyCode == KeyEvent.KEYCODE_SYM) {
                    switchVariation()
                    EventBus.getDefault().cancelEventDelivery(event)
                    return
                }

                if(event.originalKeyCode == KeyEvent.KEYCODE_SPACE && event.shiftPressed) {
                    switchInputMethod(false)
                    EventBus.getDefault().cancelEventDelivery(event)
                    return
                }
            }
        }

        if(event.originalKeyCode != KeyEvent.KEYCODE_LANGUAGE_SWITCH) inputAfterSwitch = true
    }

    @Subscribe
    fun onCandidateSelect(event: CandidateSelectEvent) {
        if(!event.methodInfo.match(currentMethod.info)) return

        currentInputConnection?.setComposingText(event.selected.text, 1)
        currentInputConnection?.finishComposingText()
        if(event.selected.endingSpace) currentInputConnection?.commitText(" ", 1)

        EventBus.getDefault().post(InputStartEvent())
    }

    private fun updateCurrentMethod() {
        currentMethod = inputMethods[InputMethodInfo.match(inputMethods.keys.toList(), InputMethodInfo(
                language = languageCycleTable[contextLanguageIndex ?: languageCycleIndex],
                device = if(physicalKeyboardPresent) InputMethodInfo.Device.PHYSICAL else InputMethodInfo.Device.VIRTUAL,
                type = variationCycleTable[variationCycleIndex],
                direct = directInputMode)).first()]!!
    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {
        variationCycleIndex = 0

        // If context language is set, unset it.
        contextLanguageIndex?.let { index ->
            languageCycleIndex = index
            contextLanguageIndex = null
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window!!.attributes.token

        if(++languageCycleIndex >= languageCycleTable.size) {
            languageCycleIndex = 0
            if(!inputAfterSwitch && switchBetweenApps) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToNextInputMethod(false)
                else imm.switchToNextInputMethod(token, false)
            }
        }

        inputAfterSwitch = false
        EventBus.getDefault().post(InputStartEvent())
    }

    private fun switchVariation() {
        if(++variationCycleIndex >= variationCycleTable.size) variationCycleIndex = 0

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

    private fun appendInputHistory(keyCode: Int, action: LBoardKeyEvent.Action): List<LBoardKeyEvent.Action> {
        val history = inputHistory[keyCode] ?: mutableListOf()
        history += action
        inputHistory += keyCode to history
        return history.toList()
    }

    private fun removeInputHistory(keyCode: Int) {
        inputHistory -= keyCode
    }

    fun init() {
        EventBus.getDefault().register(this)
        reloadPreferences()
    }

    fun destroy() {
        inputMethods.values.forEach { it.destroy() }
        candidateViewManager?.destroy()
        EventBus.getDefault().unregister(this)
        inputMethods.clear()
        candidateViewManager = null
    }

    enum class Orientation {
        PORTRAIT, LANDSCAPE
    }

    companion object {

        var INSTANCE: LBoardService? = null

        val SOFT_LAYOUT_MODE_MOBILE = listOf(
                MobileSoftLayout.LAYOUT_12KEY_4COLS,
                MobileSoftLayout.LAYOUT_15KEY_A,
                MobileSoftLayout.LAYOUT_15KEY_B,
                MobileSoftLayout.LAYOUT_15KEY_A_WITH_NUM,
                MobileSoftLayout.LAYOUT_15KEY_B_WITH_NUM,
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
                MobileSoftLayout.LAYOUT_12KEY_4COLS
        )

        val SOFT_LAYOUT_15KEY = listOf(
                MobileSoftLayout.LAYOUT_15KEY_A,
                MobileSoftLayout.LAYOUT_15KEY_B,
                MobileSoftLayout.LAYOUT_15KEY_A_WITH_NUM,
                MobileSoftLayout.LAYOUT_15KEY_B_WITH_NUM
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
