package com.rn1.gogoyo.model.source

import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.ArticleResponse
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result


class GogoyoRepositoryImpl(
    private val remoteDataSource: GogoyoDataSource,
    private val localDataSource: GogoyoDataSource
): GogoyoRepository{

    override suspend fun login(id: String, name: String): Result<Boolean> {
        return remoteDataSource.login(id, name)
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        return remoteDataSource.newPets(pet, userId)
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
        return remoteDataSource.getAllPetsByUserId(userId)
    }

    override suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>> {
        return remoteDataSource.getPetsByIdList(idList)
    }

    override suspend fun postArticle(article: Articles): Result<Boolean> {
        return remoteDataSource.postArticle(article)
    }

    override suspend fun getAllArticle(): Result<List<Articles>> {
        return remoteDataSource.getAllArticle()
    }

    override suspend fun getArticlesById(id: String): Result<List<Articles>> {
        return remoteDataSource.getArticlesById(id)
    }

    override suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>> {
        return remoteDataSource.getFavoriteArticlesById(id)
    }

    override fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>> {
        return remoteDataSource.getRealTimeResponse(articleId)
    }

    override suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>> {
        return remoteDataSource.responseArticle(articleId, response)
    }

}