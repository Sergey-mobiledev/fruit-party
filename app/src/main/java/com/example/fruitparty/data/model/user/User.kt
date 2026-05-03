package com.example.fruitparty.data.model.user

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "@username",
    val name: String = "Name",
    val surname: String = "Surname",
    val avatar: Bitmap,
    val credits: Int = 1000,
    val lineal: Int = 1,
    val rateLineal: Int = 1,
    var isSpinningMode: Boolean = false,
    val currentWin: Int = 0,
    val bonusWin: Int = 0
)