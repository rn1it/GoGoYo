package com.rn1.gogoyo.model.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*
import com.rn1.gogoyo.model.source.GogoyoDataSource

class GogoyoLocalDataSource(val context: Context) : GogoyoDataSource {
    override suspend fun login(id: String, name: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): Result<Users> {
        TODO("Not yet implemented")
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPetsById(id: String): Result<Pets> {
        TODO("Not yet implemented")
    }

    override suspend fun editPets(pet: Pets): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun editUsers(user: Users): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>> {
        TODO("Not yet implemented")
    }

    override suspend fun postArticle(article: Articles): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllArticle(): Result<List<Articles>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArticlesById(id: String): Result<List<Articles>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>> {
        TODO("Not yet implemented")
    }

    override fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>> {
        TODO("Not yet implemented")
    }

    override fun getRealTimeArticle(articleId: String): MutableLiveData<Articles> {
        TODO("Not yet implemented")
    }

    override suspend fun collectArticle(articleId: String, userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertWalk(walk: Walk): Result<Walk> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWalk(walk: Walk): Result<Walk> {
        TODO("Not yet implemented")
    }

    override suspend fun setWalkingStatus(userId: String, isWalking: Boolean): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getRealTimeOthersWalkingList(userId: String): MutableLiveData<List<Walk>> {
        TODO("Not yet implemented")
    }

    override suspend fun getOthersWalkingList(userId: String): Result<List<Walk>> {
        TODO("Not yet implemented")
    }

//    override suspend fun getWalkingList(): Result<List<Walk>> {
//        TODO("Not yet implemented")
//    }

}