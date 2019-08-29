package io.github.lee0701.lboard.dictionary

import android.content.res.AssetManager
import android.os.AsyncTask
import io.github.lee0701.lboard.inputmethod.PredictiveInputMethod
import io.github.lee0701.lboard.prediction.SQLiteDictionaryPredictor
import java.io.BufferedReader
import java.io.InputStreamReader

class BuildDictTask(val assets: AssetManager, val db: SQLiteDictionary, val fileName: String, val method: PredictiveInputMethod): AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val br = BufferedReader(InputStreamReader(assets.open(fileName)))

        db.writableDatabase.beginTransaction()

        val statement = db.writableDatabase.compileStatement("insert into ${SQLiteDictionary.WORD_TABLE_NAME}" +
                " (${SQLiteDictionary.COLUMN_METHOD_ID}, ${SQLiteDictionary.COLUMN_WORD}, ${SQLiteDictionary.COLUMN_POS}, ${SQLiteDictionary.COLUMN_FREQUENCY}, ${SQLiteDictionary.COLUMN_SEQ})" +
                " values (?, ?, ?, ?, ?)")

        while(true) {
            val line = br.readLine() ?: break
            val split = line.split('\t')
            val word = split[0]
            val pos = if(split.size >= 2) split[1] else ""
            val freq = if(split.size >= 3) split[2].toInt() else 0
            val seq = method.getSequence(word)

            statement.bindLong(1, (method.predictor as SQLiteDictionaryPredictor).methodId.toLong())
            statement.bindString(2, word)
            statement.bindString(3, pos)
            statement.bindLong(4, freq.toLong())
            statement.bindString(5, seq)

            statement.executeInsert()
        }

        db.writableDatabase.setTransactionSuccessful()
        db.writableDatabase.endTransaction()

        return null
    }
}
