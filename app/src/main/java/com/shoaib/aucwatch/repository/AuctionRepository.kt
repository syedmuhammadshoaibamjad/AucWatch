package com.shoaib.aucwatch.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.snapshots
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

class AuctionRepository {
    private val auctionCollection = FirebaseFirestore.getInstance().collection("Auction")

    suspend fun saveAuction(auctionWatch: AuctionWatchModelClass): Result<Boolean> {
        return try {
            val document = auctionCollection.document()
            auctionWatch.id = document.id
            document.set(auctionWatch).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuctionRepository", "Error saving auction: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getAuctions(): Flow<List<AuctionWatchModelClass>> {
        return auctionCollection.snapshots().map { snapshot ->
            snapshot.toObjects(AuctionWatchModelClass::class.java)
        }.distinctUntilChanged()
    }

    fun getAuctionById(auctionId: String): Flow<AuctionWatchModelClass?> {
        return auctionCollection.document(auctionId).snapshots().map { snapshot ->
            snapshot.toObject<AuctionWatchModelClass>()
        }
    }

//    * Update the bidding price for a specific auction. */
    suspend fun updateAuctionBiddingPrice(auctionId: String, newBiddingPrice: Int): Result<Boolean> {
        return try {
            val document = auctionCollection.document(auctionId)
            val auctionSnapshot = document.get().await()
            if (auctionSnapshot.exists()) {
                document.update("biddingprice", newBiddingPrice).await()
                Result.success(true)
            } else {
                Result.failure(Exception("Auction not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteAuction(auctionId: String): Result<Boolean> {
        return try {
            auctionCollection.document(auctionId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuctionRepository", "Error deleting auction: ${e.message}", e)
            Result.failure(e)
        }
    }
}
