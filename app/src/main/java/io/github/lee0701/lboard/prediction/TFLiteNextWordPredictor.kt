package io.github.lee0701.lboard.prediction

import android.content.Context
import io.github.lee0701.lboard.ml.Model
import io.github.lee0701.lboard.prediction.NextWordPredictor.Word
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class TFLiteNextWordPredictor(context: Context, wordsFileName: String): NextWordPredictor {

    private val model = Model.newInstance(context)
    private val idToWord = context.assets.open(wordsFileName).bufferedReader().readLines()
            .filter { it.trim().isNotEmpty() }
            .map { it.split('\t') }
            .map { (id, word, freq) -> id.toInt() to (word.split('/') to freq.toInt()) }
            .let { list -> list.map { (id, value) -> id to Word(id, value.first[0], value.first[1], value.second.toFloat() / list[0].second.second) } }
            .toMap()
    private val textAndPosToWord = idToWord.map { (_, word) -> (word.text to word.pos) to word }.toMap()
    private val textToWords = idToWord.values.groupBy { it.text }.mapValues { (_, list) -> list.toSet() }.toMap()

    override fun getWord(text: String, pos: String?): Word? {
        if(text == " ") return idToWord[0]
        if(pos == null) return getWords(text).maxByOrNull { it.frequency }
        else return textAndPosToWord[text to pos]
    }

    override fun getWord(id: Int): Word? {
        return idToWord[id]
    }

    override fun getWords(text: String): Set<Word> {
        return textToWords[text] ?: emptySet()
    }

    override fun predict(words: List<Int>): Map<Int, Float> {
        val input = TensorBuffer.createFixedSize(intArrayOf(1, 99), DataType.FLOAT32)
        input.loadArray(((0 until 99).map { idToWord.size } + words).takeLast(99).toIntArray())
        val output = model.process(input).outputFeature0AsTensorBuffer
        return output.floatArray.mapIndexed { i, confidence -> i to confidence }.toMap()
    }

}