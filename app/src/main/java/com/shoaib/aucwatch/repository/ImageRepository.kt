package com.shoaib.aucwatch.repository

import com.shoaib.aucwatch.datasource.cloudinary

class ImageRepository {
    // Function to upload a file to Cloudinary
    fun uploadImage(filePath: String, onComplete: (Boolean, String?) -> Unit) {
        // Call the CloudinaryUploadHelper to upload the file
        cloudinary().uploadFile(filePath) { success, result ->
            if (success) {
                // If upload is successful, return the URL of the uploaded file
                onComplete(true, result)
            } else {
                // If upload fails, return the error message
                onComplete(false, result)
            }
        }
    }
}
