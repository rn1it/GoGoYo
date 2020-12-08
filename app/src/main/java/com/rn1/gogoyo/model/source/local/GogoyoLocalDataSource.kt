package com.rn1.gogoyo.model.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.ArticleResponse
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoDataSource

class GogoyoLocalDataSource(val context: Context) : GogoyoDataSource {
    override suspend fun login(id: String, name: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
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

}