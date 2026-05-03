package com.example.fruitparty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fruitparty.data.database.dao.FireStoreModelDao
import com.example.fruitparty.data.database.dao.UserDao
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.model.user.User

@Database(entities = [User::class, FireStoreModelWithUrl::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val getUserDao: UserDao
    abstract val getFireStoreModelDaoDao: FireStoreModelDao
}