package com.shoaib.aucwatch.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shoaib.aucwatch.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignupActivity: AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var binding: ActivitySignupBinding
    lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= AuthViewModel()

        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Please wait while we check your credentials...")
        progressDialog.setCancelable(false)


        lifecycleScope.launch {
            viewModel.currentUser.collect {
                if (progressDialog!=null)progressDialog.dismiss()
                if (it != null) {
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.failureMessage.collect {
                if (progressDialog!=null)progressDialog.dismiss()
                if (it != null) {
                    Toast.makeText(this@SignupActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.loginTxt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.signUpbtn.setOnClickListener {
            val email=binding.email.editText?.text.toString()
            val password=binding.password.editText?.text.toString()
            val confirPassword=binding.confirmPassword.editText?.text.toString()
            val name=binding.name.editText?.text.toString()

            if(name.toString().trim().isEmpty()){
                Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!email.contains("@")){
                Toast.makeText(this,"Invalid Email",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password.length<6){
                Toast.makeText(this,"Password must be atleast 6 characters",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.equals(confirPassword)){
                Toast.makeText(this,"Confirm password does not match with password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            progressDialog.show()
            viewModel.signUp(email,password,name)
        }
    }


}