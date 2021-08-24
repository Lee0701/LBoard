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
            .map { (id, value) -> id to Word(id, value.first[0], value.first[1], value.second) }
            .toMap()
    private val textAndPosToWord = idToWord.map { (_, word) -> (word.text to word.pos) to word }.toMap()
    private val textToWord = idToWord.values.groupBy { it.text }.toMap()

    override fun getWord(text: String, pos: String?): Word? {
        if(text == " ") return idToWord[0]
        if(pos == null) return textToWord[text]?.maxByOrNull { it.frequency }
        else return textAndPosToWord[text to pos]
    }

    override fun getWord(id: Int): Word? {
        return idToWord[id]
    }

    override fun predict(words: List<Word>): Set<NextWordPredictor.Candidate> {
        val input = TensorBuffer.createFixedSize(intArrayOf(1, 99), DataType.FLOAT32)
        input.loadArray(((0 until 99).map { words.size } + words.map { it.id }).takeLast(99).toIntArray())
        val output = model.process(input).outputFeature0AsTensorBuffer
        return output.floatArray.mapIndexed { i, confidence -> this.idToWord[i]?.let { NextWordPredictor.Candidate(it, confidence) } }.filterNotNull().toSet()
    }

}