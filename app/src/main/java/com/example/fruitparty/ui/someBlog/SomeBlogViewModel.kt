package com.example.fruitparty.ui.someBlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.model.blog.Blog
import com.example.fruitparty.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class SomeBlogViewModel(private val repository: Repository, private val blogTitle: String) :
    ViewModel() {

    private val _liveDataSomeBlog = MutableLiveData<Blog>()
    val liveDataSomeBlog: LiveData<Blog> = _liveDataSomeBlog

    fun subscribeSomeBlog() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sharedFlowSomeBlog.collect {
                _liveDataSomeBlog.postValue(it)
            }
        }
    }

    fun emitSomeBlog() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitSomeBlog(blogTitle)
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