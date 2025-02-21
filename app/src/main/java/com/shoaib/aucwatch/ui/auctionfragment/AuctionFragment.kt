package com.shoaib.aucwatch.ui.auctionfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.shoaib.aucwatch.databinding.FragmentAuctionBinding
import com.shoaib.aucwatch.ui.addauction.AddWatchAuction
import com.shoaib.aucwatch.ui.main.BiddingActivity
import com.shoaib.aucwatch.ui.main.Details

class AuctionFragment : Fragment() {

    private lateinit var binding: FragmentAuctionBinding
    private lateinit var viewModel: AuctionFragmentViewModel
    private lateinit var adapter: AuctionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuctionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuctionFragmentViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AuctionAdapter(
            items = mutableListOf(),
            onItemClick = { auction ->
                val intent = Intent(requireContext(), Details::class.java).apply {
                    putExtra(AUCTION_ID_KEY, auction.id)
                }
                startActivity(intent)
            },

            onBidClick = { auction ->
                val intent = Intent(requireContext(), BiddingActivity::class.java).apply {
                    putExtra(AUCTION_ID_KEY, auction.id)
                    putExtra("userName", auction.userName)
                    putExtra("auction_title", auction.title)
                    putExtra("auction_starting_price", auction.startingprice)
                    putExtra("auction_bidding_price", auction.biddingprice)
                    putExtra("auction_image", auction.image)
                }
                startActivityForResult(intent, REQUEST_CODE_BID)
            }
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AuctionFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.auctions.observe(viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                binding.recyclerview.visibility = View.GONE
            } else {
                binding.recyclerview.visibility = View.VISIBLE
                adapter.updateData(items)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_BID && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val updatedBid = data.getIntExtra(BIDDING_PRICE_KEY, -1)
            val auctionId = data.getStringExtra(AUCTION_ID_KEY)
            val updatedUserName = data.getStringExtra(USER_NAME_KEY)

            if (updatedBid > -1 && !auctionId.isNullOrEmpty()) {
                val index = viewModel.auctions.value?.indexOfFirst { it.id == auctionId }

                if (index != null && index >= 0) {
                    viewModel.updateBiddingPrice(auctionId, updatedBid, updatedUserName)
                    adapter.notifyItemChanged(index)
                } else {
                    Log.w("onActivityResult", "Auction ID not found in the list.")
                }
            } else {
                Log.w("onActivityResult", "Invalid bid or auction ID.")
            }
        } else {
            Log.d("onActivityResult", "Request cancelled or invalid result.")
        }
    }

    private fun setupListeners() {
        if (isUserAdmin()) {
            binding.floatingActionButton.visibility = View.VISIBLE
            binding.floatingActionButton.setOnClickListener {
                val intent = Intent(requireContext(), AddWatchAuction::class.java)
                startActivity(intent)
            }
        } else {
            binding.floatingActionButton.visibility = View.GONE
        }
    }

    private fun isUserAdmin(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val adminEmail = "sp22-bcs-109@students.cuisahiwal.edu.pk"
        return currentUser?.email?.equals(adminEmail, ignoreCase = true) == true
    }

    companion object {
        const val REQUEST_CODE_BID = 100
        const val AUCTION_ID_KEY = "AUCTION_ID"
        const val BIDDING_PRICE_KEY = "bidding_price"
        const val USER_NAME_KEY = "userName"
    }
}
