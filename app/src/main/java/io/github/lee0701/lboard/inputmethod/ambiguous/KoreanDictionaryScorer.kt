package io.github.lee0701.lboard.inputmethod.ambiguous

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.prediction.Candidate
import java.text.Normalizer

class KoreanDictionaryScorer(val dictionary: Dictionary): Scorer {

    override fun calculateScore(string: String): Float {
        val words = string.mapIndexed { i, _ ->
            val result = mutableListOf<Dictionary.Word>()
            for(j in i .. string.length) {
                val word = string.substring(i, j)
                if(word.isEmpty()) continue
                result += dictionary.search(Normalizer.normalize(word, Normalizer.Form.NFD))
                        .map { it.copy(text = Normalizer.normalize(it.text, Normalizer.Form.NFC)) }
                        .sortedByDescending { it.frequency }
                        .distinctBy { it.text }
            }
            result
        }

        val result = mutableListOf("" to (0f to 0))

        words.forEachIndexed { i, list ->
            val targets = result.filter { it.first.length == i }
            result -= targets
            result += targets.flatMap { target ->
                list.map { target.first + it.text to (target.second.first + it.frequency to target.second.second + 1) }
            }
        }

        return result
                .map { it.first to (it.second.first / it.second.second / it.second.second to it.second.second) }
                .sortedBy { it.second.second }
                .sortedByDescending { it.second.first }
                .firstOrNull()?.second?.first ?: 0f
    }

}
