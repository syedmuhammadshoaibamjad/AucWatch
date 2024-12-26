package com.shoaib.aucwatch.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.shoaib.aucwatch.databinding.FragmentUserBinding
import com.shoaib.aucwatch.ui.auth.AuthViewModel
import com.shoaib.aucwatch.ui.auth.LoginActivity

class UserFragment : Fragment() {

    lateinit var viewModel: AuthViewModel
    lateinit var binding: FragmentUserBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Observe the currentUser flow
        lifecycleScope.launchWhenStarted {
            viewModel.currentUser.collect { user ->
                // Safely handle the nullable FirebaseUser
                user?.let {
                    // User is not null, update UI with user information
                    binding.userName.text = it.displayName ?: "N/A"
                    binding.userEmail.text = it.email ?: "N/A"
                } ?: run {
                    // Handle the case where user is null
                    binding.userName.text = "No user logged in"
                    binding.userEmail.text = "Please login"
                }
            }
        }
        // Set click listeners for buttons
        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(requireContext(),UpdateUser::class.java))
        }
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            Toast.makeText(context, "Log Out sucessfully", Toast.LENGTH_SHORT).show()
        }
    }
}
