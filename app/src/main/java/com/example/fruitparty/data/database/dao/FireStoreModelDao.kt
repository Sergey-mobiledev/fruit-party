package com.example.fruitparty.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl

@Dao
interface FireStoreModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFireStoreModel(fireStoreModelWithUrl: FireStoreModelWithUrl)

    @Query("SELECT * FROM fireStoreModel LIMIT 1")
    suspend fun getFireStoreModel(): FireStoreModelWithUrl?

    @Query("UPDATE fireStoreModel SET articleUrl=:articleUrl")
    suspend fun updateArticleUrl(articleUrl: String)

    @Query("DELETE FROM fireStoreModel")
    suspend fun clearFireStoreModel()
}