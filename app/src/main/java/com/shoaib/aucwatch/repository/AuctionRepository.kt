package com.shoaib.aucwatch.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.snapshots
import com.shoaib.aucwatch.Model.AuctionWatchModelClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AuctionRepository {

    private val auctionCollection = FirebaseFirestore.getInstance().collection("Auction")
    private val TAG = "AuctionRepository"

    private fun logError(message: String, e: Exception) {
        Log.e(TAG, message, e)
    }

    suspend fun saveAuction(auctionWatch: AuctionWatchModelClass): Result<Boolean> {
        return try {
            val document = auctionCollection.document()
            auctionWatch.id = document.id
            document.set(auctionWatch).await()
            Result.success(true)
        } catch (e: Exception) {
            logError("Error saving auction", e)
            Result.failure(e)
        }
    }


    fun getAuctions(): Flow<List<AuctionWatchModelClass>> {
        return auctionCollection
            .whereEqualTo("sold",false)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(AuctionWatchModelClass::class.java)
            }
            .catch { e ->
                // Log the error or handle it
                Log.e("Firestore", "Error fetching sold items", e)
                emit(emptyList()) // Emit an empty list in case of error
            }
    }


    fun getAuctionById(auctionId: String): Flow<AuctionWatchModelClass?> {
        return auctionCollection.document(auctionId).snapshots().map { snapshot ->
            snapshot.toObject<AuctionWatchModelClass>()
        }
    }

    suspend fun updateAuctionBiddingPrice(auctionId: String, newBiddingPrice: Int, updatedUserName: String?
    ): Result<Boolean> {
        return try {
            if (auctionId.isBlank()) {
                return Result.failure(Exception("Invalid auction ID"))
            }

            val updates = mutableMapOf<String, Any>(
                "biddingprice" to newBiddingPrice
            )

            updatedUserName?.let {
                updates["userName"] = it
            }

            auctionCollection.document(auctionId).update(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            logError("Failed to update auction bidding price", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAuction(auctionId: String): Result<Boolean> {
        return try {
            auctionCollection.document(auctionId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            logError("Error deleting auction", e)
            Result.failure(e)
        }
    }

    suspend fun markAsSold(auctionId: String): Result<AuctionWatchModelClass?> {
        return try {
            val auction = fetchAuctionById(auctionId)
            auction?.let {
                it.isSold = true
                auctionCollection.document(auctionId).set(it).await()
                Result.success(it)
            } ?: Result.failure(Exception("Auction not found"))
        } catch (e: Exception) {
            logError("Error marking auction as sold", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchAuctionById(auctionId: String): AuctionWatchModelClass? {
        val snapshot = auctionCollection.document(auctionId).get().await()
        return if (snapshot.exists()) snapshot.toObject(AuctionWatchModelClass::class.java) else null
    }

    fun sold(): Flow<List<AuctionWatchModelClass>> {
        return auctionCollection
            .whereEqualTo("sold", true)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(AuctionWatchModelClass::class.java)
            }
            .catch { e ->
                // Log the error or handle it
                Log.e("Firestore", "Error fetching sold items", e)
                emit(emptyList()) // Emit an empty list in case of error
            }
    }


    suspend fun getUsernameById(userId: String): Result<String> {
        return try {
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            if (userDocument.exists()) {
                userDocument.getString("username")?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Username field is null"))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            logError("Error fetching username", e)
            Result.failure(e)
        }
    }
}
