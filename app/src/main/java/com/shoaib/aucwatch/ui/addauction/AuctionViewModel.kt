package com.shoaib.aucwatch.ui.addauction


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shoaib.aucwatch.repository.AuctionRepository
import com.shoaib.aucwatch.repository.AuthRepository
import com.shoaib.aucwatch.repository.ImageRepository
import com.shoaib.aucwatch.Model.AuctionWatchModelClass
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SaveAuctionState {
    object Uninitialized : SaveAuctionState()
    object Success : SaveAuctionState()
    data class Failure(val message: String) : SaveAuctionState()
}

class AuctionViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val auctionRepository = AuctionRepository()
    private val imageRepository = ImageRepository()

    val currentUser = MutableStateFlow<FirebaseUser?>(authRepository.getCurrentUser())

    val isSuccessfullySaved = MutableStateFlow<Boolean?>(null) // Nullable to indicate uninitialized state
    val failureMessage = MutableStateFlow<String?>(null)

    private val _saveAuctionState = MutableStateFlow<SaveAuctionState>(SaveAuctionState.Uninitialized)
    val saveAuctionState: StateFlow<SaveAuctionState> = _saveAuctionState.asStateFlow()

    private val _data = MutableStateFlow<List<AuctionWatchModelClass>>(emptyList())
    val data: StateFlow<List<AuctionWatchModelClass>> = _data.asStateFlow()

    private val _markAsSoldResult = MutableSharedFlow<Result<AuctionWatchModelClass?>>()
    val markAsSoldResult: SharedFlow<Result<AuctionWatchModelClass?>> get() = _markAsSoldResult

    fun saveAuction(auctionWatchModelClass: AuctionWatchModelClass) {
        viewModelScope.launch {
            try {
                val result = auctionRepository.saveAuction(auctionWatchModelClass)
                if (result.isSuccess) {
                    isSuccessfullySaved.value = true
                    failureMessage.value = null // Clear previous failure messages
                } else {
                    failureMessage.value = result.exceptionOrNull()?.message
                    isSuccessfullySaved.value = false
                }
            } catch (exception: Exception) {
                failureMessage.value = exception.message
                isSuccessfullySaved.value = false
            }
        }
    }

    fun uploadAndSaveImage(realPath: String, auctionWatch: AuctionWatchModelClass) {
        imageRepository.uploadImage(realPath) { success, result ->
            viewModelScope.launch {
                if (success) {
                    auctionWatch.image = result!!
                    saveAuction(auctionWatch)
                } else {
                    _saveAuctionState.value = SaveAuctionState.Failure(result ?: "Unknown error")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            currentUser.value = null
            _data.value = emptyList()
            _saveAuctionState.value = SaveAuctionState.Uninitialized
        }
    }

    fun markAuctionAsSold(auctionId: String) {
        viewModelScope.launch {
            val result = auctionRepository.markAsSold(auctionId)
            _markAsSoldResult.emit(result) // Use emit for SharedFlow
        }
    }
}
