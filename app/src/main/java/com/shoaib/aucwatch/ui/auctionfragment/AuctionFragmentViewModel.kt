package com.shoaib.aucwatch.ui.auctionfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.aucwatch.repository.AuctionRepository
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import kotlinx.coroutines.launch


class AuctionFragmentViewModel : ViewModel() {
    private val repository = AuctionRepository()

    private val _auctions = MutableLiveData<List<AuctionWatchModelClass>>()
    val auctions: LiveData<List<AuctionWatchModelClass>> get() = _auctions

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        fetchAuctions()
    }

    // Fetch all auctions with real-time updates
    private fun fetchAuctions() {
        viewModelScope.launch {
            repository.getAuctions().collect { auctionList ->
                _auctions.postValue(auctionList)
            }
        }
    }

    // Update the bidding price of an auction
    fun updateBiddingPrice(auctionId: String, newBiddingPrice: Int) {
        viewModelScope.launch {
            try {
                val result = repository.updateAuctionBiddingPrice(auctionId, newBiddingPrice)
                if (result.isSuccess) {
                    fetchAuctions() // Refresh auctions to reflect updated bidding price
                } else {
                    _errorMessage.postValue(result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }
}



