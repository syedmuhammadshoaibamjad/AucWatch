//package com.shoaib.aucwatch.ui.main
//
//import com.bumptech.glide.Glide
//
//class WatchFragmentAdapter(
//        private var items: MutableList<AuctionWatchModelClass>,
//        private val onItemClick: (AuctionWatchModelClass) -> Unit
//    ) : RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder>() {
//
//        // ViewHolder for Auction Items
//        inner class AuctionViewHolder(private val binding: ItemAuctionBinding) :
//            RecyclerView.ViewHolder(binding.root) {
//
//            fun bind(auction: AuctionWatchModelClass) {
//                // Set the title, winner name, and ending price
//                binding.watchTitle.text = auction.title ?: "No Title"
//                binding.winnerName.text = "Winner: ${auction.winnerEmail ?: "N/A"}"
//                binding.auctionEndingPrice.text = "Final Price: $${auction.endingPrice ?: 0}"
//
//                // Load the image using Glide
//                Glide.with(binding.root.context)
//                    .load(auction.imageUrl) // Replace `imageUrl` with your actual image field
//                    .placeholder(com.shoaib.aucwatch.R.drawable.image_placeholder)
//                    .into(binding.watchImage)
//
//                // Set item click listener
//                binding.root.setOnClickListener {
//                    onItemClick(auction)
//                }
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionViewHolder {
//            val binding = ItemAuctionBinding.inflate(
//                LayoutInflater.from(parent.context), parent, false
//            )
//            return AuctionViewHolder(binding)
//        }
//
//        override fun onBindViewHolder(holder: AuctionViewHolder, position: Int) {
//            holder.bind(items[position])
//        }
//
//        override fun getItemCount(): Int = items.size
//
//        fun updateData(newItems: List<AuctionWatchModelClass>) {
//            items.clear()
//            items.addAll(newItems)
//            notifyDataSetChanged()
//        }
//    }
//
//}