package com.shoaib.aucwatch.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shoaib.aucwatch.R
import com.shoaib.aucwatch.databinding.ItemDesignBinding
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import com.shoaib.aucwatch.ui.addauction.AddWatchAuction

class ItemAdapter(private val items: List<AuctionWatchModelClass>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

//        holder.binding.imageView4.setImageResource(R.drawable.logo)
//        holder.binding.title.text = item.title ?: "No Title"
//        holder.binding.description.text = item.description ?: "No Description"
//        holder.binding.startingprice.text = item.startingprice.toString() ?: "No Starting Price"
//        holder.binding.biddingprice.text = item.biddingprice.toString() ?: "No Bidding Price"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddWatchAuction::class.java)
            intent.putExtra("id", item.id)
            holder.itemView.context.startActivity(intent)
        }
    }
}
