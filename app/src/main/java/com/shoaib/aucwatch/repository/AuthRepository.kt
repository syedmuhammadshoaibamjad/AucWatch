package com.shoaib.aucwatch.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun getUser(){
        val user= FirebaseAuth.getInstance().currentUser
        val name=user?.displayName
        val email=user?.email
        val photoUrl=user?.photoUrl

    }

    suspend fun logout(): Result<Boolean> {
        FirebaseAuth.getInstance().signOut()
        return Result.success(true)
    }


    suspend fun login(email: String, password: String):Result<FirebaseUser> {
        try {
            val result = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            return Result.success(result.user!!)
        }catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun signup(email: String,password: String,name:String): Result<FirebaseUser?> {
        try {
            val result= FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).await()
            val profileUpdate= userProfileChangeRequest {
                displayName=name
            }
            result.user?.updateProfile(profileUpdate)?.await()
            return Result.success(result.user!!)

        }catch (e:Exception){
            return Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String):Result<Boolean>{
        try {
            val result= FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
            return Result.success(true)
        } catch(e:Exception){
            return Result.failure(e)
        }


    }
}





