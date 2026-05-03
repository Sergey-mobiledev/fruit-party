package com.example.fruitparty.data.model.element

import com.example.fruitparty.R
import com.example.fruitparty.data.services.Constants.APPLE
import com.example.fruitparty.data.services.Constants.CHERRY
import com.example.fruitparty.data.services.Constants.COCONUT
import com.example.fruitparty.data.services.Constants.FRUIT_PARTY
import com.example.fruitparty.data.services.Constants.MANGO
import com.example.fruitparty.data.services.Constants.ORANGE
import com.example.fruitparty.data.services.Constants.PEACH
import com.example.fruitparty.data.services.Constants.STRAWBERRY
import com.example.fruitparty.data.services.Constants.WATERMELON

data class Element(
    val name: String,
    val image: Int,
    val weight: Int,
    val x3: Int? = null,
    val x4: Int? = null,
    val x5: Int? = null,
    var isWin: Boolean = false
) {
    companion object {
        val elements = listOf(
            Element(
                name = APPLE,
                image = R.drawable.image_apple,
                weight = 30,
                x3 = 10,
                x4 = 30,
                x5 = 100
            ),
            Element(
                name = CHERRY,
                image = R.drawable.image_cherry,
                weight = 125,
                x3 = 2,
                x4 = 5,
                x5 = 20
            ),
            Element(
                name = MANGO,
                image = R.drawable.image_mango,
                weight = 30,
                x3 = 10,
                x4 = 30,
                x5 = 100
            ),
            Element(
                name = ORANGE,
                image = R.drawable.image_orange,
                weight = 50,
                x3 = 5,
                x4 = 10,
                x5 = 50
            ),
            Element(
                name = PEACH,
                image = R.drawable.image_peach,
                weight = 17,
                x3 = 20,
                x4 = 50,
                x5 = 200
            ),
            Element(
                name = STRAWBERRY,
                image = R.drawable.image_strawberry,
                weight = 55
            ),
            Element(
                name = WATERMELON,
                image = R.drawable.image_watermelon,
                weight = 6,
                x3 = 30,
                x4 = 100,
                x5 = 500
            ),
            Element(
                name = COCONUT,
                image = R.drawable.image_coconut,
                weight = 17,
                x3 = 20,
                x4 = 50,
                x5 = 200
            ),
            Element(
                name = FRUIT_PARTY,
                image = R.drawable.image_fruit_party,
                weight = 1,
                x3 = 100,
                x4 = 1000,
                x5 = 5000
            )
        )
    }
}