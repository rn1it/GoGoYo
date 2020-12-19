package com.rn1.gogoyo.model.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*


class GogoyoRepositoryImpl(
    private val remoteDataSource: GogoyoDataSource,
    private val localDataSource: GogoyoDataSource
): GogoyoRepository{

    override suspend fun getImageUri(filePath: String): Result<String> {
        return remoteDataSource.getImageUri(filePath)
    }

    override suspend fun getVideoUri(uri: Uri): Result<String> {
        return remoteDataSource.getVideoUri(uri)
    }

    override suspend fun getAudioUri(uri: Uri): Result<String> {
        return remoteDataSource.getAudioUri(uri)
    }

    override suspend fun login(id: String, name: String): Result<Boolean> {
        return remoteDataSource.login(id, name)
    }

    override fun getLiveUserById(id: String): MutableLiveData<Users> {
        return remoteDataSource.getLiveUserById(id)
    }

    override fun getLiveUserFriendStatusById(id: String): MutableLiveData<List<Friends>> {
        return remoteDataSource.getLiveUserFriendStatusById(id)
    }

    override suspend fun getAllUsers(id: String?): Result<List<Users>> {
        return remoteDataSource.getAllUsers(id)
    }

    override suspend fun updateUser(user: Users): Result<Users> {
        return remoteDataSource.updateUser(user)
    }

    override suspend fun getUserById(id: String): Result<Users> {
        return remoteDataSource.getUserById(id)
    }

    override suspend fun getUsersById(idList: List<String>): Result<List<Users>> {
        return remoteDataSource.getUsersById(idList)
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        return remoteDataSource.newPets(pet, userId)
    }

    override suspend fun editPets(pet: Pets): Result<Boolean> {
        return remoteDataSource.editPets(pet)
    }

    override suspend fun editUsers(user: Users): Result<Boolean> {
        return remoteDataSource.editUsers(user)
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
        return remoteDataSource.getAllPetsByUserId(userId)
    }

    override suspend fun getPetsById(id: String): Result<Pets> {
        return remoteDataSource.getPetsById(id)
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

    override fun getRealTimeArticle(articleId: String): MutableLiveData<Articles> {
        return remoteDataSource.getRealTimeArticle(articleId)
    }

    override suspend fun collectArticle(articleId: String, userId: String): Result<Boolean> {
        return remoteDataSource.collectArticle(articleId, userId)
    }

    override suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>> {
        return remoteDataSource.responseArticle(articleId, response)
    }

    override suspend fun getWalkListByUserId(userId: String): Result<List<Walk>> {
        return remoteDataSource.getWalkListByUserId(userId)
    }

    override suspend fun getWalkListInfoByWalkList(walks: List<Walk>): Result<List<Walk>> {
        return remoteDataSource.getWalkListInfoByWalkList(walks)
    }

    override suspend fun insertWalk(walk: Walk): Result<Walk> {
        return remoteDataSource.insertWalk(walk)
    }

    override suspend fun updateWalk(walk: Walk): Result<Walk> {
        return remoteDataSource.updateWalk(walk)
    }

    override suspend fun setWalkingStatus(userId: String, isWalking: Boolean): Result<Boolean> {
        return remoteDataSource.setWalkingStatus(userId, isWalking)
    }

    override fun getRealTimeOthersWalkingList(userId: String): MutableLiveData<List<Walk>> {
        return remoteDataSource.getRealTimeOthersWalkingList(userId)
    }

    override suspend fun getOthersWalkingList(userId: String): Result<List<Walk>> {
        return remoteDataSource.getOthersWalkingList(userId)
    }

    override suspend fun getUserFriends(userId: String, status: Int?): Result<List<Friends>> {
        return remoteDataSource.getUserFriends(userId, status)
    }

    override suspend fun setUserFriend(userId: String, friend: Friends): Result<Friends> {
        return remoteDataSource.setUserFriend(userId, friend)
    }

    override suspend fun getChatRoom(userId: String, friendId: String): Result<Chatroom> {
        return remoteDataSource.getChatRoom(userId, friendId)
    }

    override suspend fun updateChatRoom(chatroom: Chatroom): Result<Boolean> {
        return remoteDataSource.updateChatRoom(chatroom)
    }

    override fun getUserChatList(userId: String): MutableLiveData<List<Chatroom>> {
        return remoteDataSource.getUserChatList(userId)
    }

    override suspend fun getChatRoomListWithUserInfo(list: List<Chatroom>): Result<List<Chatroom>> {
        return remoteDataSource.getChatRoomListWithUserInfo(list)
    }

    override suspend fun getChatRoomMessages(chatroomId: String): Result<List<Messages>> {
        return remoteDataSource.getChatRoomMessages(chatroomId)
    }

    override fun getLiveChatRoomMessages(chatroomId: String): MutableLiveData<List<Messages>> {
        return remoteDataSource.getLiveChatRoomMessages(chatroomId)
    }

    override suspend fun sendMessage(chatroomId: String, message: Messages): Result<Boolean> {
        return remoteDataSource.sendMessage(chatroomId, message)
    }

//    override suspend fun getWalkingList(): Result<List<Walk>> {
//        return remoteDataSource.getWalkingList()
//    }

}