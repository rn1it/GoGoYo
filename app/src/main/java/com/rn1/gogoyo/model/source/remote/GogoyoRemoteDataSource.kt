package com.rn1.gogoyo.model.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoDataSource
import com.rn1.gogoyo.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object GogoyoRemoteDataSource: GogoyoDataSource{

    private val db = FirebaseFirestore.getInstance()

    override suspend fun login(id: String, name: String): Result<Boolean> = suspendCoroutine { continuation ->

        var user : Users? = null
        val users = db.collection("users")

        users.document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user = task.result.toObject(Users::class.java)

                if (user == null) {
                    user = Users(id, name)
                    users.document(id).set(user!!).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Logger.i("Sign up: $user")
                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {e ->
                                Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                continuation.resume(Result.Error(e))
                            }
                            continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                        }
                    }
                } else {
                    Logger.i("Login: $user")
                    continuation.resume(Result.Success(true))
                }

            } else {
                task.exception?.let {e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }
}