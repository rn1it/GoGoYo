package com.rn1.gogoyo.model.source.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.*
import com.rn1.gogoyo.model.source.CurrentWeatherResponse
import com.rn1.gogoyo.model.source.GogoyoDataSource
import com.rn1.gogoyo.model.source.WeatherResponse
import com.rn1.gogoyo.network.GogoyoApi
import com.rn1.gogoyo.util.Logger
import java.io.File
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.rn1.gogoyo.util.Util.isInternetConnected

object GogoyoRemoteDataSource: GogoyoDataSource {

    private const val KEY_CREATED_TIME = "createdTime"
    private const val KEY_COLLECTION_MESSAGE = "message"
    private const val KEY_COLLECTION_FRIEND_LIST = "friendList"

    private val db = FirebaseFirestore.getInstance()
    private val usersRef = db.collection("users")
    private val petsRef = db.collection("pets")
    private val articleRef = db.collection("articles")
    private val walkRef = db.collection("walks")
    private val chatRoomRef = db.collection("chatrooms")


    private var storageRef = FirebaseStorage.getInstance().reference


    override suspend fun getImageUri(filePath: String): Result<String> =
        suspendCoroutine { continuation ->


            val file = Uri.fromFile(File(filePath))
            val imagesRef = storageRef.child("images/${file.lastPathSegment}")
            val uploadTask = imagesRef.putFile(file)

            uploadTask
                .addOnSuccessListener { taskSnapshot ->
                    // firebase path
                    val storagePath = taskSnapshot.metadata?.path as String

                    // get token
                    storageRef.child(storagePath).downloadUrl

                        .addOnSuccessListener {
                            val uri = it
                            continuation.resume(Result.Success(uri.toString()))
                        }

                        .addOnFailureListener {
                            continuation.resume(Result.Error(it))
                        }
                }

                .addOnFailureListener {
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun getVideoUri(uri: Uri): Result<String> = suspendCoroutine { continuation ->

        val videoRef = storageRef.child("videos/${System.currentTimeMillis()}")
        val uploadTask = videoRef.putFile(uri)

        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                Logger.d("addOnSuccessListener1")
                // firebase path
                val storagePath = taskSnapshot.metadata?.path as String

                // get token
                storageRef.child(storagePath).downloadUrl

                    .addOnSuccessListener {
                        Logger.d("addOnSuccessListener2")
                        val remoteUri = it
                        continuation.resume(Result.Success(remoteUri.toString()))
                    }

                    .addOnFailureListener {
                        Logger.d("addOnFailureListener2")
                        continuation.resume(Result.Error(it))
                    }
            }

            .addOnFailureListener {
                Logger.d("addOnFailureListener1")
                continuation.resume(Result.Error(it))
            }

    }

    override suspend fun getAudioUri(uri: Uri): Result<String> = suspendCoroutine { continuation ->
        val audioRef = storageRef.child("audios/${System.currentTimeMillis()}")
        val uploadTask = audioRef.putFile(uri)

        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                Logger.d("addOnSuccessListener1")
                // firebase path
                val storagePath = taskSnapshot.metadata?.path as String

                // get token
                storageRef.child(storagePath).downloadUrl

                    .addOnSuccessListener {
                        Logger.d("addOnSuccessListener2")
                        val remoteUri = it
                        continuation.resume(Result.Success(remoteUri.toString()))
                    }

                    .addOnFailureListener {
                        Logger.d("addOnFailureListener2")
                        continuation.resume(Result.Error(it))
                    }
            }

            .addOnFailureListener {
                Logger.d("addOnFailureListener1")
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun login(id: String, name: String): Result<Users> =
        suspendCoroutine { continuation ->

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
                                continuation.resume(Result.Success(user!!))
                            } else {
                                task.exception?.let { e ->
                                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                    continuation.resume(Result.Error(e))
                                }
                                continuation.resume(
                                    Result.Fail(
                                        GogoyoApplication.instance.getString(
                                            R.string.something_wrong
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        UserManager.userName = user!!.name
                        Logger.i("Login: $user")
                        continuation.resume(Result.Success(user!!))
                    }

                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    override fun getLiveUserById(id: String): MutableLiveData<Users> {

        val liveData = MutableLiveData<Users>()

        usersRef.document(id).addSnapshotListener { snapshot, exception ->

            Logger.i("addSnapshotListener detect")

            exception?.let {
                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }

            val user = snapshot!!.toObject(Users::class.java)

            liveData.value = user
        }

        return liveData
    }

    override fun getLiveUserFriendStatusById(id: String): MutableLiveData<List<Friends>> {

        val liveData = MutableLiveData<List<Friends>>()

        usersRef.document(id).collection(KEY_COLLECTION_FRIEND_LIST)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val friends = snapshot!!.toObjects(Friends::class.java)

                liveData.value = friends
            }

        return liveData
    }

    /**
     * get all users with or without login user, if id != null : get user exclude self
     */
    override suspend fun getAllUsers(id: String?): Result<List<Users>> =
        suspendCoroutine { continuation ->

            if (id.isNullOrBlank()) {
                usersRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val users = task.result.toObjects(Users::class.java)
                        Logger.w("get all user")
                        continuation.resume(Result.Success(users))
                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            } else {
                usersRef.whereNotEqualTo("id", id).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val users = task.result.toObjects(Users::class.java)
                        Logger.w("get all user")
                        continuation.resume(Result.Success(users))
                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            }
        }

    override suspend fun updateUser(user: Users): Result<Users> = suspendCoroutine { continuation ->

        usersRef.document(user.id).set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.w("update user : $user")
                continuation.resume(Result.Success(user))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override suspend fun getUserById(id: String): Result<Users> = suspendCoroutine { continuation ->

        usersRef.document(id).get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = task.result.toObject(Users::class.java)!!
                continuation.resume(Result.Success(user))

            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    /**
     * get users with pet info by id list
     */
    override suspend fun getUsersById(idList: List<String>): Result<List<Users>> =
        suspendCoroutine { continuation ->

            usersRef.whereIn("id", idList).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val users = task.result.toObjects(Users::class.java)
                    var count = 0
                    for (user in users) {
                        Logger.w("user  =  $user")

                        if (user.petIdList.isNotEmpty()) {
                            var countPet = 0
                            val petList = mutableListOf<Pets>()
                            for (petId in user.petIdList) {
                                Logger.w("petId  =  $petId")
                                petsRef.document(petId).get().addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        val pet = task2.result.toObject(Pets::class.java)!!
                                        petList.add(pet)
                                        Logger.w("petList  =  $petList")
                                        countPet += 1
                                        if (countPet == user.petIdList.size) {
                                            user.pets = petList
                                            count += 1
                                            if (count == users.size) {
                                                continuation.resume(Result.Success(users))
                                            }
                                        }
                                    } else {
                                        task2.exception?.let { e ->
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                            continuation.resume(Result.Error(e))
                                        }
                                        continuation.resume(
                                            Result.Fail(
                                                GogoyoApplication.instance.getString(
                                                    R.string.something_wrong
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            count += 1
                            if (count == users.size) {
                                continuation.resume(Result.Success(users))
                            }
                        }
                    }
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val pets = db.collection("pets")
            val document = pets.document()
            pet.id = document.id

            //add new pet
            document.set(pet).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("create pet: $pet")

                    //add new pet to user
                    usersRef.document(userId)
                        .update("petIdList", FieldValue.arrayUnion(pet.id))
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Logger.i("add pet to user: ${pet.id}")
                                continuation.resume(Result.Success(true))
                            } else {
                                task2.exception?.let { e ->
                                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                    continuation.resume(Result.Error(e))
                                }
                                continuation.resume(
                                    Result.Fail(
                                        GogoyoApplication.instance.getString(
                                            R.string.something_wrong
                                        )
                                    )
                                )
                            }
                        }
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> =
        suspendCoroutine { continuation ->

            usersRef.document(userId)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.toObject(Users::class.java)
                        val list = mutableListOf<Pets>()

                        // wait for all data set to the list, resume when all data ready
                        var count = 0

                        if (user!!.petIdList.isEmpty()) {
                            continuation.resume(Result.Success(list))
                        }

                        for (petId in user!!.petIdList!!) {
                            petsRef.document(petId).get().addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    list.add(task2.result.toObject(Pets::class.java)!!)
                                    count += 1
                                    if (count == user.petIdList!!.size) {
                                        continuation.resume(Result.Success(list))
                                    }

                                } else {
                                    task2.exception?.let { e ->
                                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                        continuation.resume(Result.Error(e))
                                    }
                                    continuation.resume(
                                        Result.Fail(
                                            GogoyoApplication.instance.getString(
                                                R.string.something_wrong
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
        }

    override suspend fun getPetsById(id: String): Result<Pets> = suspendCoroutine { continuation ->

        petsRef.document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val pet = task.result.toObject(Pets::class.java)!!
                continuation.resume(Result.Success(pet))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override suspend fun editPets(pet: Pets): Result<Boolean> = suspendCoroutine { continuation ->

        petsRef.document(pet.id).set(pet).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.i("edit pet: $pet")
                continuation.resume(Result.Success(true))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override suspend fun editUsers(user: Users): Result<Boolean> =
        suspendCoroutine { continuation ->

            usersRef.document(user.id).set(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("edit user: $user")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    override suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>> =
        suspendCoroutine { continuation ->

            val list = mutableListOf<Pets>()
            var count = 0

            if (idList.isEmpty()) {
                continuation.resume(Result.Success(list))
            }

            for (id in idList) {
                petsRef.document(id).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        list.add(task.result.toObject(Pets::class.java)!!)
                        Logger.w("add pet to list: pet id = $id")

                        count += 1
                        if (count == idList.size) {
                            Logger.w("add pet complete: idListSize = ${idList.size}, add = ${list.size}")
                            continuation.resume(Result.Success(list))
                        }

                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            }
        }

    override suspend fun postArticle(article: Articles): Result<Boolean> =
        suspendCoroutine { continuation ->

            val document = articleRef.document()
            article.id = document.id
            article.createdTime = Calendar.getInstance().timeInMillis

            document.set(article).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.w("post success: article = $article")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    /**
     * get all article include user info
     */
    override suspend fun getAllArticle(): Result<List<Articles>> =
        suspendCoroutine { continuation ->

            articleRef.orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Articles>()

                        var count = 0

                        if (task.result!!.size() == 0) {
                            continuation.resume(Result.Success(list))
                        }

                        for (document in task.result!!) {
                            Logger.w(document.id + " => " + document.data)

                            val article = document.toObject(Articles::class.java)

                            usersRef.document(article.authorId!!).get()
                                .addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        val user = task1.result.toObject(Users::class.java)!!
                                        article.author = user

                                        list.add(article)
                                        count += 1

                                        if (count == task.result!!.size()) {
                                            continuation.resume(Result.Success(list))
                                        }
                                    } else {
                                        task1.exception?.let { e ->
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                            continuation.resume(Result.Error(e))
                                        }
                                        continuation.resume(
                                            Result.Fail(
                                                GogoyoApplication.instance.getString(
                                                    R.string.something_wrong
                                                )
                                            )
                                        )
                                    }

                                }
                        }

                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }

        }

    override suspend fun getArticlesById(id: String): Result<List<Articles>> =
        suspendCoroutine { continuation ->

            articleRef.orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING).whereEqualTo(
                "authorId",
                id
            ).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Articles>()

                    var count = 0

                    if (task.result!!.size() == 0) {
                        continuation.resume(Result.Success(list))
                    }

                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val article = document.toObject(Articles::class.java)

                        usersRef.document(article.authorId!!).get().addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val user = task1.result.toObject(Users::class.java)!!
                                article.author = user

                                list.add(article)
                                count += 1

                                if (count == task.result!!.size()) {
                                    continuation.resume(Result.Success(list))
                                }
                            } else {
                                task1.exception?.let { e ->
                                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                    continuation.resume(Result.Error(e))
                                }
                                continuation.resume(
                                    Result.Fail(
                                        GogoyoApplication.instance.getString(
                                            R.string.something_wrong
                                        )
                                    )
                                )
                            }

                        }
                    }

                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }

        }

    override suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>> =
        suspendCoroutine { continuation ->

            articleRef.orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING).whereArrayContains(
                "favoriteUserIdList",
                id
            ).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Articles>()

                    var count = 0

                    if (task.result!!.size() == 0) {
                        continuation.resume(Result.Success(list))
                    }

                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val article = document.toObject(Articles::class.java)

                        usersRef.document(article.authorId!!).get().addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val user = task1.result.toObject(Users::class.java)!!
                                article.author = user

                                list.add(article)
                                count += 1

                                if (count == task.result!!.size()) {
                                    continuation.resume(Result.Success(list))
                                }
                            } else {
                                task1.exception?.let { e ->
                                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                    continuation.resume(Result.Error(e))
                                }
                                continuation.resume(
                                    Result.Fail(
                                        GogoyoApplication.instance.getString(
                                            R.string.something_wrong
                                        )
                                    )
                                )
                            }

                        }
                    }

                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }

    override fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>> {
        val liveData = MutableLiveData<List<ArticleResponse>>()

        articleRef.document(articleId).addSnapshotListener { snapshot, exception ->

            Logger.i("addSnapshotListener detect")

            exception?.let {
                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }

            val list = mutableListOf<ArticleResponse>()
            val article = snapshot!!.toObject(Articles::class.java)!!
            Logger.d("article = $article")

            list.addAll(article.responseList)

            liveData.value = list
        }

        return liveData
    }

    override fun getRealTimeArticle(articleId: String): MutableLiveData<Articles> {
        val liveData = MutableLiveData<Articles>()

        articleRef.document(articleId).addSnapshotListener { snapshot, exception ->

            Logger.i("addSnapshotListener detect")

            exception?.let {
                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }

            val article = snapshot!!.toObject(Articles::class.java)!!
            Logger.d("article = $article")


            liveData.value = article
        }

        return liveData
    }

    override suspend fun collectArticle(articleId: String, userId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            articleRef.document(articleId).get().addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    val articles = task1.result.toObject(Articles::class.java)!!

                    if (articles.favoriteUserIdList.contains(userId)) {
                        articleRef.document(articleId).update(
                            "favoriteUserIdList", FieldValue.arrayRemove(
                                userId
                            )
                        )
                        continuation.resume(Result.Success(false))
                    } else {
                        articleRef.document(articleId).update(
                            "favoriteUserIdList", FieldValue.arrayUnion(
                                userId
                            )
                        )
                        continuation.resume(Result.Success(true))
                    }
                } else {
                    task1.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }

            }


        }

    override suspend fun responseArticle(
        articleId: String,
        response: ArticleResponse
    ): Result<List<ArticleResponse>> = suspendCoroutine { continuation ->

        response.createdTime = Calendar.getInstance().timeInMillis

        articleRef.document(articleId).update("responseList", FieldValue.arrayUnion(response))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    articleRef.document(articleId).get().addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            val list = task1.result.toObject(Articles::class.java)!!.responseList
                            Logger.w("response list = $list")
                            continuation.resume(Result.Success(list))
                        } else {
                            task1.exception?.let { e ->
                                Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                continuation.resume(Result.Error(e))
                            }
                            continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                        }
                    }

                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
    }

    override suspend fun setResponseUserImage(list: List<ArticleResponse>): Result<List<ArticleResponse>> = suspendCoroutine { continuation ->

        val responseList = mutableListOf<ArticleResponse>()

        var count = 0

        if (list.isEmpty()) {
            continuation.resume(Result.Success(responseList))
        }

        for (resp in list) {
            usersRef.whereEqualTo("id", resp.userId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.toObjects(Users::class.java)[0]
                    resp.userImg = user.image
                    resp.userName = user.name
                    responseList.add(resp)
                    count += 1
                    if(count == list.size) {
                        continuation.resume(Result.Success(responseList))
                    }
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }
    }

    override suspend fun getWalkListByUserId(userId: String): Result<List<Walk>> = suspendCoroutine { continuation ->

        walkRef
            .whereEqualTo("userId", userId)
            .whereNotEqualTo("endTime", null)
            .orderBy("endTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val walks = task.result.toObjects(Walk::class.java)
                    continuation.resume(Result.Success(walks))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
    }

    override suspend fun getWalkListInfoByWalkList(walks: List<Walk>): Result<List<Walk>> = suspendCoroutine { continuation ->

        val walkList = mutableListOf<Walk>()

        var countWalk = 0

        if (walks.isEmpty()) {
            continuation.resume(Result.Success(walkList))
        }

        for (walk in walks) {

            val petIdList = walk.petsIdList

            if (petIdList.isNotEmpty()) {

                petsRef.whereIn("id", petIdList).get().addOnCompleteListener { task2 ->

                    if (task2.isSuccessful) {
                        val pets = task2.result.toObjects(Pets::class.java)
                        walk.pets = pets

                        walkList.add(walk)
                        countWalk += 1
                        if (countWalk == walks.size) {
                            continuation.resume(Result.Success(walkList))
                        }
                    } else {
                        task2.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            } else {
                walkList.add(walk)
                countWalk += 1
                if (countWalk == walks.size) {
                    continuation.resume(Result.Success(walkList))
                }
            }
        }
    }

    override suspend fun getWalkListUserInfoByWalkList(walks: List<Walk>): Result<List<Walk>> = suspendCoroutine { continuation ->

        val walkList = mutableListOf<Walk>()

        var countWalk = 0

        if (walks.isEmpty()) {
            continuation.resume(Result.Success(walkList))
        }

        for (walk in walks) {

            usersRef.whereEqualTo("id", walk.userId).get().addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val user = task.result.toObjects(Users::class.java)[0]
                    walk.user = user

                    walkList.add(walk)
                    countWalk += 1
                    if (countWalk == walks.size) {
                        continuation.resume(Result.Success(walkList))
                    }
                }else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
        }
    }

    override suspend fun insertWalk(walk: Walk): Result<Walk> = suspendCoroutine { continuation ->

        val document = walkRef.document()
        walk.id = document.id
        walk.createdTime = Calendar.getInstance().timeInMillis

        document.set(walk).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Logger.d("insert into walk: $walk")
                continuation.resume(Result.Success(walk))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }

    }

    override suspend fun updateWalk(walk: Walk): Result<Walk> = suspendCoroutine{ continuation ->

        walkRef.document(walk.id).set(walk).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Logger.d("update walk: $walk")
                continuation.resume(Result.Success(walk))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override suspend fun setWalkingStatus(userId: String, isWalking: Boolean): Result<Boolean> = suspendCoroutine{ continuation ->

        usersRef.document(userId).update("isWalking", isWalking).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Logger.d("user: $userId walk status: $isWalking")
                continuation.resume(Result.Success(true))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override fun getRealTimeOthersWalkingList(userId: String): MutableLiveData<List<Walk>> {

        val liveData = MutableLiveData<List<Walk>>()

        walkRef
//            .whereNotEqualTo("userId", userId)
            .whereEqualTo("endTime", null)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Walk>()
                for (document in snapshot!!) {
                    Logger.d(document.id + " => " + document.data)
                    val walk = document.toObject(Walk::class.java)

                    list.add(walk)
                }

                liveData.value = list
        }

        return liveData

    }

    override suspend fun getOthersWalkingList(userId: String): Result<List<Walk>> = suspendCoroutine { continuation ->

        walkRef
            .whereNotEqualTo("userId", userId)
            .whereEqualTo("endTime", null)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Walk>()

                    for (document in task.result) {
                        val walk = document.toObject(Walk::class.java)
                        list.add(walk)
                    }
                    Logger.w("online walk list = $list")
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))

                }
            }


    }

    override fun getUserLiveFriend(userId: String, status: Int?): MutableLiveData<List<Friends>> {
        val liveData = MutableLiveData<List<Friends>>()

        if (status == null) {
            usersRef.document(userId)
                .collection("friendList")
                .addSnapshotListener { snapshot, exception ->

                    Logger.i("addSnapshotListener detect")
                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }

                    val list = mutableListOf<Friends>()
                    for (document in snapshot!!) {
                        Logger.d(document.id + " => " + document.data)
                        val walk = document.toObject(Friends::class.java)
                        list.add(walk)
                    }

                    liveData.value = list
                }

        } else {
            usersRef.document(userId)
                .collection("friendList")
                .whereEqualTo("status", status)
                .addSnapshotListener { snapshot, exception ->

                    Logger.i("addSnapshotListener detect")
                    exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    }

                    val list = mutableListOf<Friends>()
                    for (document in snapshot!!) {
                        Logger.d(document.id + " => " + document.data)
                        val walk = document.toObject(Friends::class.java)
                        list.add(walk)
                    }

                    liveData.value = list
                }
        }

        return liveData
    }

    override suspend fun getUserFriends(userId: String, status: Int?): Result<List<Friends>> = suspendCoroutine { continuation ->

        if (status == null) {
            usersRef.document(userId)
                .collection("friendList")
                .orderBy("status", Query.Direction.DESCENDING)
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = task.result.toObjects(Friends::class.java)
                    Logger.w("getUserFriends = $list")
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }

            }
        } else {
            usersRef.document(userId)
                .collection("friendList")
                .whereEqualTo("status", status)
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = task.result.toObjects(Friends::class.java)
                    Logger.w("getUserFriends = $list")
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }

            }
        }

    }

    override suspend fun setUserFriend(userId: String, friend: Friends): Result<Friends> = suspendCoroutine { continuation ->

        usersRef.document(userId)
            .collection(KEY_COLLECTION_FRIEND_LIST)
            .document(friend.friendId)
            .set(friend)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("set user friend : $friend")
                    continuation.resume(Result.Success(friend))
                } else {
                    task.exception?.let { e ->
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                        continuation.resume(Result.Error(e))
                    }
                    continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                }
            }
    }

    override suspend fun getChatRoom(userId: String, friendId: String): Result<Chatroom> = suspendCoroutine { continuation ->

//        chatRoomRef.whereArrayContainsAny("userList", listOf(userId, friendId)).get().addOnCompleteListener { task ->
        chatRoomRef.whereArrayContains("userList", userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.size() > 0) {
                    var count = 0
                    for (document in task.result) {
                        val chatroom = document.toObject(Chatroom::class.java)
                        if (chatroom.userList.containsAll(listOf(userId, friendId))) {
                            Logger.i("get chatroom: $chatroom")
                            continuation.resume(Result.Success(chatroom))
                        } else {
                            count += 1
                            if (count == task.result.size()) {
                                val chatRoomRefDocument = chatRoomRef.document()
                                val chatRoom = Chatroom(
                                    document.id,
                                    mutableListOf(userId, friendId),
                                    Calendar.getInstance().timeInMillis
                                )

                                chatRoomRefDocument.set(chatRoom).addOnCompleteListener { task2 ->

                                    if (task2.isSuccessful) {
                                        Logger.i("create chatroom: $chatRoom")
                                        continuation.resume(Result.Success(chatRoom))

                                    } else {
                                        task2.exception?.let { e ->
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                            continuation.resume(Result.Error(e))
                                        }
                                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                                    }
                                }
                            }
                        }
                    }
                } else {

                    val document = chatRoomRef.document()
                    val chatroom = Chatroom(
                        document.id,
                        mutableListOf(userId, friendId),
                        Calendar.getInstance().timeInMillis
                        )

                    document.set(chatroom).addOnCompleteListener { task2 ->

                        if (task2.isSuccessful) {
                            Logger.i("create chatroom: $chatroom")
                            continuation.resume(Result.Success(chatroom))

                        } else {
                            task2.exception?.let { e ->
                                Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                                continuation.resume(Result.Error(e))
                            }
                            continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                        }
                    }
                }
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }

    }

    override suspend fun updateChatRoom(chatroom: Chatroom): Result<Boolean> = suspendCoroutine { continuation ->

        chatRoomRef.document(chatroom.id).set(chatroom).addOnCompleteListener { task ->

            if (task.isSuccessful){
                Logger.i("update chat room : $chatroom")
                continuation.resume(Result.Success(true))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }

        }
    }

    override fun getUserChatList(userId: String): MutableLiveData<List<Chatroom>> {

        val liveData = MutableLiveData<List<Chatroom>>()

        chatRoomRef.whereArrayContains("userList", userId).orderBy("msgTime", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->

            Logger.i("addSnapshotListener detect")

            exception?.let {
                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
            }

            val list = mutableListOf<Chatroom>()
            for (document in snapshot!!) {
                Logger.d(document.id + " => " + document.data)
                val chatroom = document.toObject(Chatroom::class.java)

                list.add(chatroom)
            }

            liveData.value = list
        }

        return liveData
    }

    override suspend fun getChatRoomListWithUserInfo(list: List<Chatroom>): Result<List<Chatroom>> = suspendCoroutine { continuation ->

        val chatRooms = mutableListOf<Chatroom>()

        if (list.isEmpty()) {
            continuation.resume(Result.Success(chatRooms))
        } else {

            var count = 0

            if (list.isEmpty()) {
                continuation.resume(Result.Success(chatRooms))
            }

            for (chatRoom in list) {

                val anotherUserId = chatRoom.userList.filter { it != UserManager.userUID }[0]
                usersRef.document(anotherUserId).get().addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val user = task.result.toObject(Users::class.java)
                        chatRoom.friend = user
                        chatRooms.add(chatRoom)
                        count += 1

                        if (count == list.size) {
                            continuation.resume(Result.Success(chatRooms))
                        }

                    } else {

                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))


                    }


                }

            }
        }

    }

    override suspend fun getChatRoomMessages(chatroomId: String): Result<List<Messages>>  = suspendCoroutine { continuation ->

        chatRoomRef.document(chatroomId).collection("messages").get().addOnCompleteListener { task ->

            if (task.isSuccessful){
                val messages = task.result.toObjects(Messages::class.java)
                Logger.i("get messages: $messages")
                continuation.resume(Result.Success(messages))
            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override fun getLiveChatRoomMessages(chatroomId: String): MutableLiveData<List<Messages>> {

        val liveData = MutableLiveData<List<Messages>>()

        chatRoomRef.document(chatroomId).collection(KEY_COLLECTION_MESSAGE)
            .orderBy("msgTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                Logger.i("addSnapshotListener detect")

                exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Messages>()
                for (document in snapshot!!) {
                    Logger.d(document.id + " => " + document.data)
                    val message = document.toObject(Messages::class.java)

                    list.add(message)
                }

                liveData.value = list
            }

        return liveData
    }

    override suspend fun getLiveChatRoomMessagesWithUserInfo(list: List<Messages>): Result<List<Messages>> = suspendCoroutine { continuation ->

        val listWithUserInfo = mutableListOf<Messages>()
        var count = 0

        if (list.isEmpty()) {
            continuation.resume(Result.Success(listWithUserInfo))
        }

        for (msg in list) {

            if (msg.senderId == UserManager.userUID) {
                usersRef.document(msg.receiverId).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userImg = task.result.toObject(Users::class.java)!!.image
                        msg.friendImg = userImg
                        listWithUserInfo.add(msg)
                        count += 1
                        if (count == list.size) {
                            continuation.resume(Result.Success(listWithUserInfo))
                        }

                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            } else {
                usersRef.document(msg.senderId).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userImg = task.result.toObject(Users::class.java)!!.image
                        msg.friendImg = userImg
                        listWithUserInfo.add(msg)
                        count += 1
                        if (count == list.size) {
                            continuation.resume(Result.Success(listWithUserInfo))
                        }
                    } else {
                        task.exception?.let { e ->
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                            continuation.resume(Result.Error(e))
                        }
                        continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
                    }
                }
            }
        }
    }

    override suspend fun sendMessage(chatroomId: String, message: Messages): Result<Boolean> = suspendCoroutine { continuation ->

        chatRoomRef.document(chatroomId).collection(KEY_COLLECTION_MESSAGE).document().set(message).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Logger.w("send msg : $message")
                continuation.resume(Result.Success(true))

            } else {
                task.exception?.let { e ->
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    continuation.resume(Result.Error(e))
                }
                continuation.resume(Result.Fail(GogoyoApplication.instance.getString(R.string.something_wrong)))
            }
        }
    }

    override suspend fun getForeCastWeather(id: String): Result<WeatherResponse> {

        if (!isInternetConnected()) {
            return Result.Fail("尚未連接 Internet")
        }

        return try {
            val result = GogoyoApi.retrofitService.getForecast(id)
            Result.Success(result)
        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }

    }

    override suspend fun getCurrentWeather(lat: Double, lng: Double): Result<CurrentWeatherResponse> {

        if (!isInternetConnected()) {
            return Result.Fail("尚未連接 Internet")
        }

        return try {
            val result = GogoyoApi.retrofitService.getCurrentWeather(lat, lng)
            Result.Success(result)
        } catch (e: Exception) {
            Logger.w("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }
}