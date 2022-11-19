package com.example.forhealth.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DatabaseViewModelFactory(private val dataRepository: DataRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DatabaseViewModel(dataRepository) as T
    }
}