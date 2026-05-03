package com.example.fruitparty.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.user.User
import com.example.fruitparty.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _liveDataCurrentFragmentId = MutableLiveData<Int>()
    val liveDataCurrentFragmentId: LiveData<Int> = _liveDataCurrentFragmentId

    private val _liveDataUser = MutableLiveData<User>()
    val liveDataUser: LiveData<User> = _liveDataUser

    fun updateName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateName(name)
        }
    }

    fun updateSurName(surName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSurName(surName)
        }
    }

    fun updateUserName(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserName(userName)
        }
    }

    fun updateUserAvatar(avatar: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserAvatar(avatar)
        }
    }

    fun subscribeUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowUser.collect {
                _liveDataUser.postValue(it)
            }
        }
    }

    fun addLineal() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLineal()
        }
    }

    fun addRateLineal() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRateLineal()
        }
    }

    fun subscribeCurrentFragmentId() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowCurrentFragmentId.collect {
                _liveDataCurrentFragmentId.postValue(it)
            }
        }
    }

    fun resetCurrentWin() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetCurrentWin()
        }
    }

    fun cancelViewModelScopeCoroutines() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}