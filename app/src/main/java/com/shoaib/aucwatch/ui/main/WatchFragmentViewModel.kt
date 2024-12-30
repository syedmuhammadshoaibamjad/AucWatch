package com.shoaib.aucwatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.aucwatch.repository.AuctionRepository
import com.shoaib.aucwatch.Model.AuctionWatchModelClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WatchFragmentViewModel : ViewModel() {
    private val repository = AuctionRepository()

    private val _data = MutableStateFlow<List<AuctionWatchModelClass>?>(null)
    val data: StateFlow<List<AuctionWatchModelClass>?> = _data

    val isFailure = MutableStateFlow<String?>(null)

    init {
        getMarkedSold()
    }

    private fun getMarkedSold() {
        viewModelScope.launch {
            repository.sold() // Assuming this method exists in your repository
                .catch { exception ->
                    isFailure.value = exception.message // Capture any errors
                }
                .collect { auction ->
                    _data.value = auction // Update the LiveData with the fetched data
                    isFailure.value = null // Clear the failure message
                }
        }
    }
}