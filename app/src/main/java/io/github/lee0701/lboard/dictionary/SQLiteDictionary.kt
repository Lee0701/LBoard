package io.github.lee0701.lboard.dictionary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import io.github.lee0701.lboard.prediction.Candidate

class SQLiteDictionary(private val openHelper: SQLiteOpenHelper) {

    private val db = openHelper.writableDatabase
    val writableDatabase: SQLiteDatabase get() = openHelper.writableDatabase

    private val selectQuery = "select $COLUMN_ID, $COLUMN_WORD, $COLUMN_POS from $WORD_TABLE_NAME" +
            " where $COLUMN_METHOD_ID=? and $COLUMN_SEQ=?" +
            " order by $COLUMN_FREQUENCY desc"

    private val updateQuery = "update $WORD_TABLE_NAME" +
            " set $COLUMN_FREQUENCY=$COLUMN_FREQUENCY+1" +
            " where $COLUMN_ID=?"

    fun addWord(word: String, seq: String, methodId: String) {
        val values = ContentValues()
        values.put(COLUMN_WORD, word)
        values.put(COLUMN_SEQ, seq)
        values.put(COLUMN_METHOD_ID, methodId)
        values.put(COLUMN_FREQUENCY, 1)

        db.insertOrThrow(WORD_TABLE_NAME, null, values)
    }

    fun updateWord(word: Candidate) {
        db.execSQL(updateQuery, arrayOf(word.wordId.toString()))
    }

    fun searchWord(seq: String, methodId: Int): List<Candidate> {
        val cursor = db.rawQuery(selectQuery, arrayOf(methodId.toString(), seq))
        val result = mutableListOf<Candidate>()

        while(cursor.moveToNext()) {
            result += Candidate(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
        }

        cursor.close()
        return result
    }

    class DatabaseHelper(val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        fun needsUpgrading(): Boolean {
            if(context.databaseList().isNotEmpty()) {
                val db = context.openOrCreateDatabase(DATABASE_NAME, 0, null)
                val version = db.version
                db.close()
                return version < DATABASE_VERSION
            } else {
                return false
            }
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                    "create table if not exists $WORD_TABLE_NAME (" +
                            "$COLUMN_ID integer primary key autoincrement, " +
                            "$COLUMN_METHOD_ID integer, " +
                            "$COLUMN_SEQ text, " +
                            "$COLUMN_WORD text, " +
                            "$COLUMN_POS text, " +
                            "$COLUMN_FREQUENCY integer )"
            )
            db.execSQL(
                    "create index if not exists idx on $WORD_TABLE_NAME (" +
                            "$COLUMN_METHOD_ID, $COLUMN_SEQ asc, $COLUMN_FREQUENCY desc )"
            )
            db.execSQL(
                    "create trigger if not exists $FREQ_TRIGGER_NAME" +
                            " after update on $WORD_TABLE_NAME" +
                            " when new.$COLUMN_FREQUENCY > $FREQ_MAX" +
                            " begin" +
                            " update $WORD_TABLE_NAME set $COLUMN_FREQUENCY = $COLUMN_FREQUENCY / $FREQ_DIV" +
                            " where $COLUMN_SEQ = new.$COLUMN_SEQ;" +
                            " end;"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }
    }

    companion object {
        const val DATABASE_NAME = "lboarddict.db"
        const val DATABASE_VERSION = 1

        const val WORD_TABLE_NAME = "word"
        const val FREQ_TRIGGER_NAME = "freqtrigger"

        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_METHOD_ID = "method"
        const val COLUMN_SEQ = "seq"
        const val COLUMN_WORD = "word"
        const val COLUMN_POS = "pos"
        const val COLUMN_FREQUENCY = "freq"

        const val FREQ_DIV = 16
        const val FREQ_MAX = 255

        private var INSTANCE: SQLiteDictionary? = null

        fun getInstance(context: Context): SQLiteDictionary {
            INSTANCE?.let { return it }
            val instance = SQLiteDictionary(DatabaseHelper(context))
            INSTANCE = instance
            return instance
        }

    }

}
