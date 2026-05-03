package com.example.fruitparty.data.database.dao

import android.graphics.Bitmap
import androidx.room.*
import com.example.fruitparty.data.model.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("UPDATE user SET name=:name")
    fun updateName(name: String): Int

    @Query("UPDATE user SET surname=:surName")
    fun updateSurName(surName: String): Int

    @Query("UPDATE user SET userName=:userName")
    fun updateUserName(userName: String): Int

    @Query("UPDATE user SET avatar=:avatar")
    fun updateUserAvatar(avatar: Bitmap): Int

    @Query("UPDATE user SET credits=:credits")
    fun updateCredits(credits: Int): Int

    @Query("UPDATE user SET currentWin=:currentWin")
    fun updateCurrentWin(currentWin: Int): Int

    @Query("UPDATE user SET bonusWin=:bonusWin")
    fun updateBonusWin(bonusWin: Int): Int

    @Query("UPDATE user SET lineal=:lineal")
    fun updateLineal(lineal: Int): Int

    @Query("UPDATE user SET rateLineal=:betLineal")
    fun updateRateLineal(betLineal: Int): Int

    @Query("UPDATE user SET isSpinningMode=:isSpinningMode")
    fun updateIsSpinningMode(isSpinningMode: Boolean): Int

    @Query("SELECT * FROM user")
    fun getFlowUser(): Flow<User>

    @Query("SELECT * FROM user")
    suspend fun getUser(): User

    @Query("SELECT lineal FROM user")
    suspend fun getLineal(): Int

    @Query("SELECT currentWin FROM user")
    suspend fun getCurrentWin(): Int

    @Query("SELECT bonusWin FROM user")
    suspend fun getBonusWin(): Int

    @Query("SELECT rateLineal FROM user")
    suspend fun getRateLineal(): Int

    @Query("SELECT credits FROM user")
    suspend fun getCredits(): Int

    @Query("SELECT isSpinningMode FROM user")
    suspend fun getIsSpinningMode(): Boolean

    @Query("DELETE FROM user")
    suspend fun clearUser()
}