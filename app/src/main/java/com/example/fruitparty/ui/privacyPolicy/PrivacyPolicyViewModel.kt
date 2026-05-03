package com.example.fruitparty.ui.privacyPolicy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fruitparty.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivacyPolicyViewModel(private val repository: Repository) : ViewModel() {

    fun emitCurrentFragment(fragmentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.emitCurrentFragmentId(fragmentId)
        }
    }
}