package com.example.fruitparty.data.repository

import android.graphics.Bitmap
import com.example.fruitparty.data.database.dao.FireStoreModelDao
import com.example.fruitparty.data.database.dao.UserDao
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.model.user.User

class DaoRepository(
    private val userDao: UserDao,
    private val fireStoreModelDao: FireStoreModelDao
) {

    suspend fun addUser(user: User) = userDao.addUser(user)
    suspend fun updateName(name: String) = userDao.updateName(name)
    suspend fun updateSurName(surName: String) = userDao.updateSurName(surName)
    suspend fun updateUserName(userName: String) = userDao.updateUserName(userName)
    suspend fun updateUserAvatar(avatar: Bitmap) = userDao.updateUserAvatar(avatar)
    suspend fun updateCredits(credits: Int) = userDao.updateCredits(credits)
    suspend fun updateCurrentWin(currentWin: Int) = userDao.updateCurrentWin(currentWin)
    suspend fun updateBonusWin(bonusWin: Int) = userDao.updateBonusWin(bonusWin)
    suspend fun updateIsSpinningMode(isSpinningMode: Boolean) =
        userDao.updateIsSpinningMode(isSpinningMode)

    suspend fun updateLineal(lineal: Int) = userDao.updateLineal(lineal)
    suspend fun updateRateLineal(betLineal: Int) = userDao.updateRateLineal(betLineal)
    suspend fun getUser() = userDao.getUser()
    suspend fun getLineal() = userDao.getLineal()
    suspend fun getRateLineal() = userDao.getRateLineal()
    suspend fun getCredits() = userDao.getCredits()
    suspend fun getIsSpinningMode() = userDao.getIsSpinningMode()
    suspend fun getCurrentWin() = userDao.getCurrentWin()
    suspend fun getBonusWin() = userDao.getBonusWin()
    fun getFlowUser() = userDao.getFlowUser()

    suspend fun addFireStoreModel(fireStoreModelWithUrl: FireStoreModelWithUrl) =
        fireStoreModelDao.addFireStoreModel(fireStoreModelWithUrl)

    suspend fun getFireStoreModel() = fireStoreModelDao.getFireStoreModel()
    suspend fun getUpdateArticleUrl(articleUrl: String) =
        fireStoreModelDao.updateArticleUrl(articleUrl)

    suspend fun clearFireStoreModel() = fireStoreModelDao.clearFireStoreModel()

}