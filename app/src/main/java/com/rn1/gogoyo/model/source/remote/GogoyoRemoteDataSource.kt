package com.rn1.gogoyo.model.source.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoDataSource
import com.rn1.gogoyo.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object GogoyoRemoteDataSource: GogoyoDataSource{

    private val db = FirebaseFirestore.getInstance()
    private val usersRef =  db.collection("users")
    private val petsRef =  db.collection("pets")
    private val articleRef = db.collection("articles")


    override suspend fun login(id: String, name: String): Result<Boolean> = suspendCoroutine { continuation ->

        var user: Users?
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
                    UserManager.userName = user!!.name
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

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> = suspendCoroutine { continuation ->

        val pets = db.collection("pets")
        val document = pets.document()
        pet.id = document.id

        //add new pet
        document.set(pet).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.i("create pet: $pet")

                //add new pet to user
                usersRef.document(userId)
                    .update("petList", FieldValue.arrayUnion(pet.id))
                    .addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            Logger.i("add pet to user: ${pet.id}")
                            continuation.resume(Result.Success(true))
                        } else {
                            task2.exception?.let {e ->
                                Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                continuation.resume(Result.Error(e))
                            }
                            continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                        }
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

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> = suspendCoroutine{ continuation ->

        usersRef.document(userId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.toObject(Users::class.java)
                    val list = mutableListOf<Pets>()

                    // wait for all data set to the list, resume when all data ready
                    var count = 0
                    for (petId in user!!.petList!!) {
                        petsRef.document(petId).get().addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                list.add(task2.result.toObject(Pets::class.java)!!)
                                count += 1
                                if (count == user.petList!!.size) {
                                    continuation.resume(Result.Success(list))
                                }

                            } else {
                                task2.exception?.let {e ->
                                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                    continuation.resume(Result.Error(e))
                                }
                                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                            }
                        }
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

    override suspend fun postArticle(article: Articles): Result<Boolean> = suspendCoroutine{ continuation ->

        val document = articleRef.document()
        article.id = document.id

        document.set(article).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.w("post success: article = $article")
                    continuation.resume(Result.Success(true))
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