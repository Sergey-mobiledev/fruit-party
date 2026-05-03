package com.example.fruitparty.data.services

class ElementResult constructor(
    val status: Status,
    val line: Int? = null,
    val x: Int? = null,
    val win: Int? = null,
    val positions: List<List<Int>>? = null

) {
    companion object {
        fun win(line: Int, x: Int, win: Int) =
            ElementResult(status = Status.WIN, line = line, x = x, win = win)

        val LOSS = ElementResult(status = Status.LOSS)
        val EMPTY_VALUE = ElementResult(status = Status.EMPTY_VALUE)
        val END_ANIMATION_BONUS_GAME = ElementResult(status = Status.END_ANIMATION_BONUS_GAME)
        fun bonusGame(positions: List<List<Int>>, win: Int) =
            ElementResult(status = Status.BONUS_GAME, positions = positions, win = win)
    }

    enum class Status {
        WIN,
        LOSS,
        BONUS_GAME,
        END_ANIMATION_BONUS_GAME,
        EMPTY_VALUE
    }
}