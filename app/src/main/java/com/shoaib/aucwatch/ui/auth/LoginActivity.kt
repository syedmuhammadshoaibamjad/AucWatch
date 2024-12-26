package com.shoaib.aucwatch.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shoaib.aucwatch.ui.main.MainActivity
import com.shoaib.aucwatch.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= AuthViewModel()


        progressDialog=ProgressDialog(this)
        progressDialog.setMessage("Please wait while we check your credentials...")
        progressDialog.setCancelable(false)



        lifecycleScope.launch {
            viewModel.currentUser.collect {
                if (it != null) {
                    Toast.makeText(this@LoginActivity, "login Successful", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.failureMessage.collect {
                progressDialog.dismiss()
                if (it!=null) {
                    Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.signupTxt.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        binding.forgetPassword.setOnClickListener{
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }


        binding.loginbtn.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val password = binding.password.editText?.text.toString()

            if (!email.contains("@")) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            progressDialog.show()
            viewModel.login(email, password)

        }
    }




}