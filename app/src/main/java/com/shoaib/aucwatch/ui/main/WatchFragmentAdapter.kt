package com.shoaib.aucwatch.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoaib.aucwatch.R
import com.shoaib.aucwatch.databinding.SoldwatchdesignBinding
import com.shoaib.aucwatch.Model.AuctionWatchModelClass

class WatchFragmentAdapter(
    private val items: List<AuctionWatchModelClass>, // Use an immutable list
    private val onItemClick: (AuctionWatchModelClass) -> Unit
) : RecyclerView.Adapter<WatchFragmentAdapter.WatchViewHolder>() {

    inner class WatchViewHolder(private val binding: SoldwatchdesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(watch: AuctionWatchModelClass, onItemClick: (AuctionWatchModelClass) -> Unit) {
            binding.watchTitle.text = watch.title ?: "No Title"
            binding.winnerName.text = "Winner: ${watch.userName ?: "N/A"}"
            binding.auctionEndingPrice.text = "Final Price: $${watch.biddingprice ?: "0"}"

            Glide.with(binding.root.context)
                .load(watch.image ?: R.drawable.logo)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(binding.image)

            binding.root.setOnClickListener {
                onItemClick(watch)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchViewHolder {
        val binding = SoldwatchdesignBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size
}
