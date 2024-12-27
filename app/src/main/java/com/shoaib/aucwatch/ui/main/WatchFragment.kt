package com.shoaib.aucwatch.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoaib.aucwatch.databinding.FragmentWatchBinding
import com.shoaib.aucwatch.ui.AuctionWatchModelClass
import com.shoaib.aucwatch.ui.auctionfragment.AuctionFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WatchFragment : Fragment() {

    private var _binding: FragmentWatchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WatchFragmentViewModel by viewModels()
    private lateinit var adapter: WatchFragmentAdapter
    private val items =ArrayList<AuctionWatchModelClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
       adapter = WatchFragmentAdapter(
            items=items,
            onItemClick = { clickedItem: AuctionWatchModelClass ->
                Toast.makeText(context, "Details for ${clickedItem.title}", Toast.LENGTH_SHORT)
                    .show()
            }
        )

                binding.recyclerview.adapter = adapter
                binding.recyclerview.layoutManager = LinearLayoutManager(context)

                // Observe failure messages
                lifecycleScope.launch {
                    viewModel.isFailure.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        lifecycleScope.launch {
            viewModel.data.collect { data ->
                data?.let {
                    items.clear()
                    items.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
            }
}
