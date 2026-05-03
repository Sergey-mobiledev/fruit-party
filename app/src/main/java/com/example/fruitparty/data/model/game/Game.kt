package com.example.fruitparty.data.model.game

import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants.FRUIT_PARTY

data class Game(
    val isOpen: Boolean,
    val name: String? = null,
    val image: Int? = null
) {
    companion object {
        val games = listOf(
            Game(
                isOpen = true,
                name = FRUIT_PARTY,
                image = R.drawable.image_game_fruit_party
            ),
            Game(
                isOpen = false
            ),
            Game(
                isOpen = false
            ),
            Game(
                isOpen = false
            )
        )
    }
}