package com.shoaib.aucwatch.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.shoaib.aucwatch.databinding.ActivityDetailsBinding
import com.shoaib.aucwatch.repository.AuctionRepository
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import com.shoaib.aucwatch.ui.addauction.AuctionViewModel
import kotlinx.coroutines.launch

class Details : AppCompatActivity() {

    private lateinit var auction: AuctionWatchModelClass
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: AuctionViewModel
    private val repository = AuctionRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuctionViewModel::class.java]

        // Deserialize Auction Data
        val data = intent.getStringExtra("data")
        if (data.isNullOrEmpty()) {
            Toast.makeText(this, "No Winner", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        auction = Gson().fromJson(data, AuctionWatchModelClass::class.java)

        // Bind Data to Views
        binding.highestprice.text = "$${auction.biddingprice}"
       binding.highestbidder.text = "Higest BidderName: ${auction.userName}"

        // Handle the Sold Button click
        binding.soldbutton.setOnClickListener {
            auction.id?.let { auctionId -> fetchSoldItem(auctionId) }
        }
    }

    private fun fetchUserName(userId: String?) {
        userId?.let {
            lifecycleScope.launch {
                val result = repository.getUsernameById(it)

                when {
                    result.isSuccess -> {
                        // Handling success case
                        val username = result.getOrNull()  // Get the value, or null if it failed
                        if (username != null) {
                            binding.highestbidder.text = "Name: $username"
                        } else {
                            binding.highestbidder.text = "Username not found"
                        }
                    }
                    result.isFailure -> {
                        // Handling failure case
                        val exception = result.exceptionOrNull()  // Get the exception
                        val errorMessage = exception?.message ?: "Unknown error"
                        Toast.makeText(this@Details, "Error fetching username: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        } ?: run {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSoldItem(auctionId: String) {
        lifecycleScope.launch {
            try {
                repository.getAuctionById(auctionId).collect { fetchedAuction ->
                    fetchedAuction?.let {
                        it.id?.let { auctionId ->
                            viewModel.markAuctionAsSold(auctionId) // Mark the auction as sold

                        }
                        Toast.makeText(this@Details, "Item sent to Watch Fragment", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    } ?: run {
                        Toast.makeText(this@Details, "Failed to fetch auction item.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Details, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
