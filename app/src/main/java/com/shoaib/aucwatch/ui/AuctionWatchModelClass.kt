package com.shoaib.aucwatch.ui

data class AuctionWatchModelClass(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var startingprice: Int = 0,
    var biddingprice: Int = 0,
    var image: String? = null,
    var highestBidder: String? = ""
)
