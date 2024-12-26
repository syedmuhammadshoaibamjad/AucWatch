package com.shoaib.aucwatch.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.shoaib.aucwatch.R

class BiddingActivity : AppCompatActivity() {

    private var biddingPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bidding)

        // Retrieve auction details passed from AuctionFragment
        val auctionId = intent.getStringExtra("AUCTION_ID") ?: ""
        val auctionTitle = intent.getStringExtra("auction_title")
        val auctionImage = intent.getStringExtra("auction_image")
        val auctionStartingPrice = intent.getIntExtra("auction_starting_price", 0)
        var currentBiddingPrice = intent.getIntExtra("auction_bidding_price", 0)

        // Find views
        val auctionTitleView = findViewById<TextView>(R.id.auction_title)
        val auctionImageView = findViewById<ImageView>(R.id.auction_image)
        val bidInputField = findViewById<TextInputEditText>(R.id.bid_input)
        val bidButton = findViewById<Button>(R.id.bid_button)
        val currentBidView = findViewById<TextView>(R.id.current_bid)

        // Display auction details
        auctionTitleView.text = auctionTitle
        currentBidView.text = "Current Highest Bid: $$currentBiddingPrice"

        if (!auctionImage.isNullOrEmpty()) {
            // Load image using Glide
            Glide.with(this)
                .load(auctionImage) // URL of the image
                .placeholder(R.drawable.logo) // Placeholder image while loading
                .into(auctionImageView)
        }

        // Set up bid button click listener
        bidButton.setOnClickListener {
            val inputText = bidInputField.text.toString()
            if (inputText.isNotEmpty()) {
                try {
                    biddingPrice = inputText.toInt()

                    when {
                        biddingPrice < auctionStartingPrice -> {
                            Toast.makeText(
                                this,
                                "Your bid must be at least the starting price ($$auctionStartingPrice).",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        biddingPrice <= currentBiddingPrice -> {
                            Toast.makeText(
                                this,
                                "Your bid must be higher than the current highest bid ($$currentBiddingPrice).",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            // Update the bidding price and display it
                            currentBiddingPrice = biddingPrice
                            currentBidView.text = "Current Highest Bid: $$currentBiddingPrice"

                            // Pass the new bid back to the calling activity/fragment
                            val resultIntent = Intent().apply {
                                putExtra("bidding_price", currentBiddingPrice)
                                putExtra("AUCTION_ID", auctionId) // Ensure the auction_id is returned
                            }

                            Toast.makeText(
                                this,
                                "Bid placed successfully: $$currentBiddingPrice",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(RESULT_OK, resultIntent)

                            // Close the activity after setting the result
                            finish()
                        }
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        this,
                        "Invalid bid. Please enter a valid number.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please enter your bid.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
