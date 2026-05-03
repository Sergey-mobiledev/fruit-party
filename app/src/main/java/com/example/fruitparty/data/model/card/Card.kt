package com.example.fruitparty.data.model.card

import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants.BLACK
import com.example.fruitparty.data.services.Constants.CLUBS
import com.example.fruitparty.data.services.Constants.DIAMONDS
import com.example.fruitparty.data.services.Constants.HEARTS
import com.example.fruitparty.data.services.Constants.RED
import com.example.fruitparty.data.services.Constants.SPADES

data class Card(
    val id: Int,
    val color: String,
    val suit: String,
    val imageOpen: Int,
    val imageClosed: Int = R.drawable.image_card_closed,
    val isOpen: Boolean = false
) {
    companion object {
        val cards = listOf(
            Card(
                id = 1,
                color = RED,
                suit = HEARTS,
                imageOpen = R.drawable.image_card_hearts,
            ),
            Card(
                id = 2,
                color = RED,
                suit = DIAMONDS,
                imageOpen = R.drawable.image_card_diamonds,
            ),
            Card(
                id = 3,
                color = BLACK,
                suit = CLUBS,
                imageOpen = R.drawable.image_card_clubs,
            ),
            Card(
                id = 4,
                color = BLACK,
                suit = SPADES,
                imageOpen = R.drawable.image_card_spades,
            )
        )
    }
}