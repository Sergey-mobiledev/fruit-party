package com.example.fruitparty.ui.bonusGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.card.Card
import com.example.fruitparty.data.repository.Repository
import com.example.fruitparty.data.services.BonusGameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BonusGameViewModel(private val repository: Repository) : ViewModel() {

    private val _liveDataCardList = MutableLiveData<List<Card>>()
    val liveDataCardList: LiveData<List<Card>> = _liveDataCardList

    private val _liveDataBonusGameState = MutableLiveData<BonusGameState>()
    val liveDataBonusGameState: LiveData<BonusGameState> = _liveDataBonusGameState

    init {
        observeBonusGameState()
        observeCardList()
    }

    fun startBonusGame() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startBonusGame()
        }
    }

    private fun observeBonusGameState() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowBonusGameState.collect {
                _liveDataBonusGameState.postValue(it)
            }
        }
    }

    fun openCard(id: Int, selectedColor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.openCard(id, selectedColor)
        }
    }

    fun emitRandomCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitRandomCardList()
        }
    }

    private fun observeCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowCardList.collect {
                _liveDataCardList.postValue(it)
            }
        }
    }

    fun emitCurrentFragment(fragmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(fragmentId)
        }
    }

    fun updateBonusWin(bonusWin: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBonusWin(bonusWin)
        }
    }
}