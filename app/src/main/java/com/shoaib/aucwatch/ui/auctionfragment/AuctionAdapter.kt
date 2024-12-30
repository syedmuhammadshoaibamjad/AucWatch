package com.shoaib.aucwatch.ui.auctionfragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shoaib.aucwatch.R;
import com.shoaib.aucwatch.databinding.ItemDesignBinding;
import com.shoaib.aucwatch.Model.AuctionWatchModelClass;
import com.shoaib.aucwatch.ui.main.Details;

class AuctionAdapter(
    private val items: MutableList<AuctionWatchModelClass>,
    private val onItemClick: (AuctionWatchModelClass) -> Unit,
    private val onBidClick: (AuctionWatchModelClass) -> Unit
) : RecyclerView.Adapter<AuctionAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(auction: AuctionWatchModelClass) = with(binding) {
            title.text = auction.title ?: "Untitled"
            description.text = auction.description ?: "No description available"
            startingprice.text = "Starting: $${auction.startingprice ?: 0}"
            biddingprice.text = "Highest Bid: $${auction.biddingprice ?: 0}"

            Glide.with(root.context)
                .load(auction.image ?: R.drawable.logo)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(imageView4)

            buttonBid.setOnClickListener { onBidClick(auction) }

            root.setOnClickListener {
                val intent = Intent(binding.root.context, Details::class.java)
                intent.putExtra("data", Gson().toJson(auction))
                binding.root.context.startActivity(intent)
                onItemClick(auction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<AuctionWatchModelClass>) {
        val diffCallback = AuctionDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}

class AuctionDiffCallback(
    private val oldList: List<AuctionWatchModelClass>,
    private val newList: List<AuctionWatchModelClass>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    /**
     * Checks if two items represent the same auction based on their unique IDs.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    /**
     * Checks if the content of two items is identical.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}