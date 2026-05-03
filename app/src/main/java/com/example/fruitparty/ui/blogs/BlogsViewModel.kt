package com.example.fruitparty.ui.blogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.repository.Repository
import com.example.fruitparty.data.services.DataLoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class BlogsViewModel(private val repository: Repository) : ViewModel() {

    private val _liveDataBlogsLoadingState = MutableLiveData<DataLoadingState>()
    val liveDataBlogsLoadingState: LiveData<DataLoadingState> = _liveDataBlogsLoadingState

    fun updateArticleUrl(articleUrl: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateArticleUrl(articleUrl)
        }
    }

    fun subscribeBlogsLoadingState() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowBlogsLoadingState.collect {
                _liveDataBlogsLoadingState.postValue(it)
            }
        }
    }

    fun subscribeFlowDataLoadingState() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.flowDataLoadingState.collect {
                _liveDataBlogsLoadingState.postValue(it)
            }
        }
    }

    fun readUrlFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readUrlFromFirebase()
        }
    }

    fun getBlogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBlogs()
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