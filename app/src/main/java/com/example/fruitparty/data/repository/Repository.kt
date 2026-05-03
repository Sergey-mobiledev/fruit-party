package com.example.fruitparty.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.fruitparty.R
import com.example.fruitparty.data.model.blog.Blog
import com.example.fruitparty.data.model.card.Card
import com.example.fruitparty.data.model.element.Element
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.model.user.User
import com.example.fruitparty.data.services.BonusGameState
import com.example.fruitparty.data.services.Constants.COCONUT
import com.example.fruitparty.data.services.Constants.FRUIT_PARTY
import com.example.fruitparty.data.services.Constants.STRAWBERRY
import com.example.fruitparty.data.services.Constants.TAG_TEST
import com.example.fruitparty.data.services.Constants.checkInternetConnection
import com.example.fruitparty.data.services.Constants.getBitmapFromVectorDrawable
import com.example.fruitparty.data.services.DataLoadingState
import com.example.fruitparty.data.services.ElementResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class Repository(
    private val context: Context,
    private val fireStoreRepository: FireStoreRepository,
    private val apiRepository: ApiRepository,
    private val daoRepository: DaoRepository
) {

    val flowDataLoadingState = fireStoreRepository.sharedFlowDataLoadingState.map { it }
    val flowUser = daoRepository.getFlowUser().map { it }
    val sharedFlowCurrentFragmentId = MutableSharedFlow<Int>()
    val sharedFlowBlogsLoadingState = MutableSharedFlow<DataLoadingState>()
    val sharedFlowSomeBlog = MutableSharedFlow<Blog>()
    val sharedFlowResult = MutableSharedFlow<ElementResult>()
    private var blogsList: List<Blog> = emptyList()
    val sharedFlowCardList = MutableSharedFlow<List<Card>>(replay = 1)
    private var currentCardList = arrayListOf<Card>()
    val sharedFlowBonusGameState = MutableSharedFlow<BonusGameState>(replay = 1)

    suspend fun updateName(name: String) = daoRepository.updateName(name)
    suspend fun updateSurName(surName: String) = daoRepository.updateSurName(surName)
    suspend fun updateUserName(userName: String) = daoRepository.updateUserName(userName)
    suspend fun updateUserAvatar(avatar: Bitmap) = daoRepository.updateUserAvatar(avatar)
    suspend fun updateCurrentWin(currentWin: Int) =
        daoRepository.updateCurrentWin(daoRepository.getCurrentWin() + currentWin)

    suspend fun resetCurrentWin() {
        if (daoRepository.getCurrentWin() != 0) {
            daoRepository.updateCurrentWin(0)
        }
    }

    suspend fun addFireStoreModel(fireStoreModelWithUrl: FireStoreModelWithUrl) =
        daoRepository.addFireStoreModel(fireStoreModelWithUrl)

    suspend fun getFireStoreModel(callback: (FireStoreModelWithUrl?) -> Unit) =
        callback(daoRepository.getFireStoreModel())

    suspend fun readUrlFromFirebase() = fireStoreRepository.readUrlFromFirebase()
    suspend fun emitCurrentFragmentId(fragmentId: Int) =
        sharedFlowCurrentFragmentId.emit(fragmentId)

    suspend fun isCreditsEnough(callback: (Boolean) -> Unit) =
        callback(daoRepository.getCredits() >= daoRepository.getLineal() * daoRepository.getRateLineal())

    suspend fun updateCreditsPlusWin(win: Int) =
        daoRepository.updateCredits(daoRepository.getCredits() + win)


    suspend fun updateCreditsPlusBonusWin() {
        val bonusWin = daoRepository.getBonusWin()
        if (bonusWin != 0) {
            daoRepository.updateCredits(daoRepository.getCredits() + bonusWin)
            daoRepository.updateBonusWin(0)
        }
    }

    suspend fun updateBonusWin(bonusWin: Int) = daoRepository.updateBonusWin(bonusWin)
    suspend fun updateArticleUrl(articleUrl: String) = daoRepository.getUpdateArticleUrl(articleUrl)

    suspend fun updateIsSpinningModel(isSpinningMode: Boolean) {
        if (isSpinningMode) {
            daoRepository.updateIsSpinningMode(true)
        } else {
            if (daoRepository.getIsSpinningMode()) {
                daoRepository.updateIsSpinningMode(false)
            }
        }
    }

    suspend fun startBonusGame() =
        sharedFlowBonusGameState.emit(BonusGameState.startBonusGame(daoRepository.getBonusWin()))

    suspend fun openCard(id: Int, selectedColor: String) {
        val index = currentCardList.indexOfFirst { it.id == id }
        if (index == -1) return
        val updatedCard = currentCardList[index].copy(isOpen = true)
        currentCardList = ArrayList(currentCardList)
        currentCardList[index] = updatedCard
        sharedFlowCardList.emit(currentCardList)
        if (updatedCard.color == selectedColor) {
            val bonusWin = daoRepository.getBonusWin() * 2
            daoRepository.updateBonusWin(bonusWin)
            delay(500)
            sharedFlowBonusGameState.emit(BonusGameState.win(bonusWin))
            delay(1575)
        } else {
            daoRepository.updateBonusWin(0)
            delay(500)
            sharedFlowBonusGameState.emit(BonusGameState.LOSS)
            delay(875)
        }
        sharedFlowBonusGameState.emit(BonusGameState.END_ANIMATION)
    }

    suspend fun emitRandomCardList() {
        currentCardList = Card.cards.shuffled().take(3) as ArrayList<Card>
        currentCardList = ArrayList(currentCardList)
        sharedFlowCardList.emit(ArrayList(currentCardList))
    }

    suspend fun readFirebase() {
        if (daoRepository.getUser() == null) {
            try {
                daoRepository.addUser(
                    User(
                        avatar = getBitmapFromVectorDrawable(
                            context,
                            R.drawable.default_avatar
                        )
                    )
                )
            } catch (e: Exception) {
                Log.d(TAG_TEST, e.toString())
            }
        }
        daoRepository.clearFireStoreModel()
        fireStoreRepository.readFirebase()
    }

    suspend fun emitSomeBlog(blogTitle: String) {
        val someBlog = blogsList.find { blogTitle == it.title.rendered }
        if (someBlog != null) {
            sharedFlowSomeBlog.emit(someBlog)
        }
    }

    suspend fun getBlogs() {
        sharedFlowBlogsLoadingState.emit(DataLoadingState.LOADING_BLOGS)
        val url = daoRepository.getFireStoreModel()?.articleUrl.orEmpty()
        if (url.isBlank()) {
            sharedFlowBlogsLoadingState.emit(DataLoadingState.errorBadUrl())
            return
        }
        if (checkInternetConnection(context)) {
            try {
                blogsList = withTimeout(3000) { apiRepository.getBlogs(url) }
                sharedFlowBlogsLoadingState.emit(DataLoadingState.loadedBlogs(blogsList))
            } catch (e: Exception) {
                if (e is TimeoutCancellationException) {
                    sharedFlowBlogsLoadingState.emit(DataLoadingState.errorTimeoutExpired())
                } else {
                    sharedFlowBlogsLoadingState.emit(DataLoadingState.errorBadUrl())
                }
            }
        } else {
            sharedFlowBlogsLoadingState.emit(DataLoadingState.errorInternetConnection())
        }
    }

    suspend fun addLineal() {
        val currentLineal = daoRepository.getLineal()
        if (currentLineal == 3) {
            daoRepository.updateLineal(1)
        } else {
            daoRepository.updateLineal(currentLineal + 1)
        }
    }

    suspend fun addRateLineal() {
        val currentRateLineal = daoRepository.getRateLineal()
        if (currentRateLineal == 30) {
            daoRepository.updateRateLineal(1)
        } else {
            daoRepository.updateRateLineal(currentRateLineal + 1)
        }
    }

    suspend fun getElementsListWithStrawberry(callback: (List<Element>) -> Unit) {
        val listElementsWithStrawberry = mutableListOf<Element>()
        Element.elements.forEach { element ->
            repeat(element.weight) {
                listElementsWithStrawberry.add(element)
            }
        }
        callback(listElementsWithStrawberry)
    }

    suspend fun getElementsListWithoutStrawberry(callback: (List<Element>) -> Unit) {
        val listElementsWithoutStrawberry = mutableListOf<Element>()
        Element.elements.forEach { element ->
            if (element.name != STRAWBERRY) {
                repeat(element.weight) {
                    listElementsWithoutStrawberry.add(element)
                }
            }
        }
        callback(listElementsWithoutStrawberry)
    }

    suspend fun updateCreditsMinusSpin() {
        val credits = daoRepository.getCredits()
        val rate = daoRepository.getLineal() * daoRepository.getRateLineal()
        val newCredits = credits - rate
        daoRepository.updateCredits(newCredits)
    }

    suspend fun checkResult(elementsMap: Map<Int, List<Element>>) {
        val lines = daoRepository.getLineal()
        val betLines = daoRepository.getRateLineal()
        val newElementsMap = mutableMapOf<Int, List<Element>>()
        when (lines) {
            1 -> {
                newElementsMap[1] = elementsMap[2]!!
            }
            2 -> {
                newElementsMap[1] = elementsMap[2]!!
                newElementsMap[2] = elementsMap[1]!!

            }
            3 -> {
                newElementsMap[1] = elementsMap[2]!!
                newElementsMap[2] = elementsMap[1]!!
                newElementsMap[3] = elementsMap[3]!!
            }
        }
        newElementsMap.forEach { (line, elements) ->
            val currentElement = elements[0]
            if (currentElement.name != FRUIT_PARTY && (currentElement.name == elements[1].name || elements[1].name == COCONUT)) {
                if (elements[1].name == elements[2].name || elements[2].name == COCONUT || (currentElement.name == elements[2].name && elements[1].name == COCONUT)) {
                    if (elements[2].name == elements[3].name || elements[3].name == COCONUT || (currentElement.name == elements[3].name && elements[2].name == COCONUT)) {
                        if (elements[3].name == elements[4].name || elements[4].name == COCONUT || (currentElement.name == elements[4].name && elements[3].name == COCONUT)) {
                            sharedFlowResult.emit(
                                ElementResult.win(
                                    line = line,
                                    x = 5,
                                    win = betLines * currentElement.x5!!
                                )
                            )
                            delay(2000)
                        } else {
                            sharedFlowResult.emit(
                                ElementResult.win(
                                    line = line,
                                    x = 4,
                                    win = betLines * currentElement.x4!!
                                )
                            )
                            delay(2000)
                        }
                    } else {
                        sharedFlowResult.emit(
                            ElementResult.win(
                                line = line,
                                x = 3,
                                win = betLines * currentElement.x3!!
                            )
                        )
                        delay(2000)
                    }
                }
            } else {
                if (currentElement.name == FRUIT_PARTY && currentElement.name == elements[1].name && currentElement.name == elements[2].name) {
                    if (currentElement.name == elements[3].name) {
                        if (currentElement.name == elements[4].name) {
                            sharedFlowResult.emit(
                                ElementResult.win(
                                    line = line,
                                    x = 5,
                                    win = betLines * currentElement.x5!!
                                )
                            )
                            delay(2000)
                        } else {
                            sharedFlowResult.emit(
                                ElementResult.win(
                                    line = line,
                                    x = 4,
                                    win = betLines * currentElement.x4!!
                                )
                            )
                            delay(2000)
                        }
                    } else {
                        sharedFlowResult.emit(
                            ElementResult.win(
                                line = line,
                                x = 3,
                                win = betLines * currentElement.x3!!
                            )
                        )
                        delay(2000)
                    }
                }
            }
        }
        checkOnStrawberries(elementsMap, betLines * lines)
    }

    private suspend fun checkOnStrawberries(elementsMap: Map<Int, List<Element>>, rate: Int) {
        var strawberry = 0
        val coordinators = mutableListOf<List<Int>>()
        for (i in 1..elementsMap.size) {
            var index = 1
            elementsMap[i]?.forEach {
                if (it.name == STRAWBERRY) {
                    strawberry += 1
                    var line = 1
                    when (i) {
                        1 -> {
                            line = 2
                        }
                        2 -> {
                            line = 1
                        }
                        3 -> {
                            line = 3
                        }
                    }
                    coordinators.add(listOf(line, index))
                }
                index += 1
            }
        }
        if (strawberry >= 3) {
            sharedFlowResult.emit(ElementResult.bonusGame(coordinators, 12 * rate))
            delay(3500)
            sharedFlowResult.emit(ElementResult.END_ANIMATION_BONUS_GAME)
        } else {
            sharedFlowResult.emit(ElementResult.LOSS)
        }
    }
}


