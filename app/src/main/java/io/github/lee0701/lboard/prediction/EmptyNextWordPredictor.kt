package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.prediction.NextWordPredictor.Word

class EmptyNextWordPredictor: NextWordPredictor {

    private val emptyWord = Word(0, "", "", 0f)

    override fun getWord(text: String, pos: String?): Word {
        return emptyWord
    }

    override fun getWord(id: Int): Word {
        return emptyWord
    }

    override fun getWords(text: String): Set<Word> {
        return emptySet()
    }

    override fun predict(words: List<Int>): Map<Int, Float> = emptyMap()
}