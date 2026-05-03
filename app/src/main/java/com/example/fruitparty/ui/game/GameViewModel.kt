package com.example.fruitparty.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.element.Element
import com.example.fruitparty.data.repository.Repository
import com.example.fruitparty.data.services.ElementResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(private val repository: Repository) : ViewModel() {

    private val _liveDataElementsResult = MutableLiveData<ElementResult>()
    val liveDataElementsResult: LiveData<ElementResult> = _liveDataElementsResult

    fun isCreditsEnough(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.isCreditsEnough(callback)
        }
    }

    fun updateCurrentWin(currentWin: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCurrentWin(currentWin)
        }
    }

    fun updateBonusWin(bonusWin: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBonusWin(bonusWin)
        }
    }

    fun resetCurrentWin() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetCurrentWin()
        }
    }

    fun checkResult(elementsMap: Map<Int, List<Element>>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkResult(elementsMap)
        }
    }

    fun subscribeElementsResult() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowResult.collect {
                _liveDataElementsResult.postValue(it)
                if (it.status == ElementResult.Status.END_ANIMATION_BONUS_GAME) {
                    delay(50)
                    _liveDataElementsResult.postValue(ElementResult.EMPTY_VALUE)
                }
            }
        }
    }

    fun updateCreditsMinusSpin() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCreditsMinusSpin()
        }
    }

    fun updateCreditsPlusWin(win: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCreditsPlusWin(win)
        }
    }

    fun updateCreditsPlusBonusWin() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCreditsPlusBonusWin()
        }
    }

    fun updateIsSpinningMode(isSpinningModel: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIsSpinningModel(isSpinningModel)
        }
    }

    fun getElementsListWithStrawberry(callback: (List<Element>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getElementsListWithStrawberry(callback)
        }
    }

    fun getElementsListWithoutStrawberry(callback: (List<Element>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getElementsListWithoutStrawberry(callback)
        }
    }

    fun emitCurrentFragment(fragmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(fragmentId)
        }
    }

    fun cancelViewModelScopeCoroutines() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}