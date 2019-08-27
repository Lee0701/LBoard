package io.github.lee0701.lboard

import io.github.lee0701.lboard.event.LBoardKeyEvent

interface InputHistoryHolder {

    val inputHistory: MutableMap<Int, MutableList<LBoardKeyEvent.Action>>

    fun appendInputHistory(keyCode: Int, action: LBoardKeyEvent.Action): List<LBoardKeyEvent.Action> {
        val history = inputHistory[keyCode] ?: mutableListOf()
        history += action
        inputHistory += keyCode to history
        return history.toList()
    }

}
