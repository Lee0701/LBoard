package io.github.lee0701.lboard.prediction

class EmptyNextWordPredictor: NextWordPredictor {

    private val emptyWord = NextWordPredictor.Word(0, "", "", 0)

    override fun getWord(text: String, pos: String?): NextWordPredictor.Word? {
        return emptyWord
    }

    override fun getWord(id: Int): NextWordPredictor.Word? {
        return emptyWord
    }

    override fun predict(words: List<NextWordPredictor.Word>): Set<NextWordPredictor.Candidate> = emptySet()
}