package com.example.fruitparty.ui.chooseGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseGameViewModel(private val repository: Repository) : ViewModel() {

    fun getFireStoreModel(callback: (FireStoreModelWithUrl?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFireStoreModel(callback)
        }
    }

    fun emitCurrentFragment(fragmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(fragmentId)
        }
    }
}