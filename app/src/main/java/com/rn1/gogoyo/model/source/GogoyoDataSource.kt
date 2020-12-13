package com.rn1.gogoyo.model.source

import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*
import kotlin.coroutines.suspendCoroutine

interface GogoyoDataSource {

    suspend fun login(id: String, name: String): Result<Boolean>

    suspend fun getUserById(id: String): Result<Users>

    suspend fun getUsersById(idList: List<String>): Result<List<Users>>

    suspend fun newPets(pet: Pets, userId: String): Result<Boolean>

    suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>>

    suspend fun getPetsById(id: String): Result<Pets>

    suspend fun editPets(pet: Pets): Result<Boolean>

    suspend fun editUsers(user: Users): Result<Boolean>

    suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>>

    suspend fun postArticle(article: Articles): Result<Boolean>

    suspend fun getAllArticle(): Result<List<Articles>>

    suspend fun getArticlesById(id: String): Result<List<Articles>>

    suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>>

    fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>>

    fun getRealTimeArticle(articleId: String): MutableLiveData<Articles>

    suspend fun collectArticle(articleId: String, userId: String): Result<Boolean>

    suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>>

    suspend fun insertWalk(walk: Walk): Result<Walk>

    suspend fun updateWalk(walk: Walk): Result<Walk>

    suspend fun setWalkingStatus(userId: String, isWalking: Boolean): Result<Boolean>

//    suspend fun getWalkingList(): Result<List<Walk>>

    fun getRealTimeOthersWalkingList(userId: String): MutableLiveData<List<Walk>>

    suspend fun getOthersWalkingList(userId: String): Result<List<Walk>>

    suspend fun getUserFriends(userId: String, status: Int?): Result<List<Friends>>

}