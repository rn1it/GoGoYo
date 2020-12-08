package com.rn1.gogoyo.model.source

import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.ArticleResponse
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result

interface GogoyoRepository {

    suspend fun login(id: String, name: String): Result<Boolean>

    suspend fun newPets(pet: Pets, userId: String): Result<Boolean>

    suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>>

    suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>>

    suspend fun postArticle(article: Articles): Result<Boolean>

    suspend fun getAllArticle(): Result<List<Articles>>

    suspend fun getArticlesById(id: String): Result<List<Articles>>

    suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>>

    fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>>

    suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>>
}