package com.example.fruitparty.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.firestore.FireStoreModelWithUrl
import com.example.fruitparty.data.repository.Repository
import com.example.fruitparty.data.services.DataLoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository) : ViewModel() {

    private val _liveDataLoadingState = MutableLiveData<DataLoadingState>()
    val liveDataLoadingState: LiveData<DataLoadingState> = _liveDataLoadingState

    fun subscribeFlowDataLoadingState() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowDataLoadingState.collect {
                _liveDataLoadingState.postValue(it)
            }
        }
    }

    fun addFireStoreModel(fireStoreModelWithUrl: FireStoreModelWithUrl) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFireStoreModel(fireStoreModelWithUrl)
        }
    }

    fun readFireBase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readFirebase()
        }
    }

    fun readUrlFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readUrlFromFirebase()
        }
    }

    fun cancelViewModelScopeCoroutines() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}