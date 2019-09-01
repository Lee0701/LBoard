package io.github.lee0701.lboard.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.Preference
import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.R
import java.io.File
import java.io.FileOutputStream

class ResetDictionaryPreference(context: Context, attrs: AttributeSet): Preference(context, attrs) {

    private val fileName = attrs.getAttributeValue(null, "fileName")

    override fun onClick() {
        val instance = LBoardService.INSTANCE
        val file = File(context.filesDir, fileName)
        if(!file.exists()) return
        instance?.destroy()
        FileOutputStream(file).write(byteArrayOf())
        instance?.init()
        Toast.makeText(context, R.string.msg_user_dictionary_reset, Toast.LENGTH_LONG).show()
    }
}
