package com.shoaib.aucwatch.ui.addauction

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shoaib.aucwatch.databinding.ActivityAddWatchAuctionBinding
import com.shoaib.aucwatch.Model.AuctionWatchModelClass
import kotlinx.coroutines.launch

class AddWatchAuction : AppCompatActivity() {

   var uri: Uri? = null
    lateinit var binding: ActivityAddWatchAuctionBinding
    lateinit var viewModel: AuctionViewModel
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWatchAuctionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuctionViewModel()

        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Please wait while ...")
        progressDialog.setCancelable(false)

        lifecycleScope.launch {
            viewModel.isSuccessfullySaved.collect {
                it?.let{
                    if (it == true) {
                        Toast.makeText(
                            this@AddWatchAuction,
                            "Successfully saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.failureMessage.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(this@AddWatchAuction, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

    binding.submit.setOnClickListener {

        val Title = binding.title.editText?.text.toString().trim()
        val Description = binding.description.editText?.text.toString().trim()
        val StartingPrice = binding.startingprice.editText?.text.toString().trim()

        if (Title.isEmpty() || Description.isEmpty() || StartingPrice.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        val StartingPriceValue = StartingPrice.toIntOrNull()

        if (StartingPriceValue == null) {
            Toast.makeText(this, "Invalid starting price", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
        progressDialog.show()

        val auctionWatch = AuctionWatchModelClass().apply {
            this.title = Title
            this.description = Description
            this.startingprice = StartingPriceValue
        }

        if (uri == null) {
            viewModel.saveAuction(auctionWatch)
        } else {
            uri?.let { imageUri ->
                val realPath = getRealPathFromURI(imageUri)
                if (realPath != null) {
                    viewModel.uploadAndSaveImage(realPath, auctionWatch)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to get image path", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

        binding.imageView3.setOnClickListener {
            chooseImageFromGallery()
        }
    }

       private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            uri = result.data?.data
            uri?.let {
                binding.imageView3.setImageURI(it)
            } ?: Log.e("Gallery", "No image selected")
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return null
    }
}
