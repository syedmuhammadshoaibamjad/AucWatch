package com.shoaib.aucwatch.ui.addauction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shoaib.aucwatch.repository.AuctionRepository
import com.shoaib.aucwatch.repository.AuthRepository
import com.shoaib.aucwatch.repository.ImageRepository
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuctionViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val auctionRepository = AuctionRepository()
    private val imageRepository = ImageRepository()
    val currentUser = MutableStateFlow<FirebaseUser?>(null)

    val isSuccessfullySaved = MutableStateFlow<Boolean?>(null) // Nullable to indicate uninitialized state
    val failureMessage = MutableStateFlow<String?>(null)


  init {

          currentUser.value = authRepository.getCurrentUser()
      }


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
            if (success) {
                auctionWatch.image = result!!
                saveAuction(auctionWatch)
            } else {
                failureMessage.value = result
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            currentUser.value = null
        }
}}
