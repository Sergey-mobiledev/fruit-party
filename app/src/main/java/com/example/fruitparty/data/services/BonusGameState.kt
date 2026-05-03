package com.example.fruitparty.data.services

class BonusGameState constructor(
    val status: Status,
    val win: Int? = null
) {
    companion object {
        fun startBonusGame(win: Int) = BonusGameState(status = Status.START_BONUS_GAME, win = win)
        fun win(win: Int) = BonusGameState(status = Status.WIN, win = win)
        val LOSS = BonusGameState(status = Status.LOSS)
        val END_ANIMATION = BonusGameState(status = Status.END_ANIMATION)
    }

    enum class Status {
        START_BONUS_GAME,
        WIN,
        LOSS,
        END_ANIMATION
    }
}