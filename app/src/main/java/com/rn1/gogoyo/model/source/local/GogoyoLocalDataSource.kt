package com.rn1.gogoyo.model.source.local

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*
import com.rn1.gogoyo.model.source.GogoyoDataSource

class GogoyoLocalDataSource(val context: Context) : GogoyoDataSource {
    override suspend fun getImageUri(filePath: String): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoUri(uri: Uri): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getAudioUri(uri: Uri): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun login(id: String, name: String): Result<Users> {
        TODO("Not yet implemented")
    }

    override fun getLiveUserById(id: String): MutableLiveData<Users> {
        TODO("Not yet implemented")
    }

    override fun getLiveUserFriendStatusById(id: String): MutableLiveData<List<Friends>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsers(id: String?): Result<List<Users>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: Users): Result<Users> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): Result<Users> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersById(idList: List<String>): Result<List<Users>> {
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

    override suspend fun setResponseUserImage(list: List<ArticleResponse>): Result<List<ArticleResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWalkListByUserId(userId: String): Result<List<Walk>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWalkListInfoByWalkList(walks: List<Walk>): Result<List<Walk>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWalkListUserInfoByWalkList(walks: List<Walk>): Result<List<Walk>> {
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

    override fun getUserLiveFriend(userId: String, status: Int?): MutableLiveData<List<Friends>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserFriends(userId: String, status: Int?): Result<List<Friends>> {
        TODO("Not yet implemented")
    }

    override suspend fun setUserFriend(userId: String, friend: Friends): Result<Friends> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(userId: String, friendId: String): Result<Chatroom> {
        TODO("Not yet implemented")
    }

    override suspend fun updateChatRoom(chatroom: Chatroom): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserChatList(userId: String): MutableLiveData<List<Chatroom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoomListWithUserInfo(list: List<Chatroom>): Result<List<Chatroom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoomMessages(chatroomId: String): Result<List<Messages>> {
        TODO("Not yet implemented")
    }

    override fun getLiveChatRoomMessages(chatroomId: String): MutableLiveData<List<Messages>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLiveChatRoomMessagesWithUserInfo(list: List<Messages>): Result<List<Messages>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chatroomId: String, message: Messages): Result<Boolean> {
        TODO("Not yet implemented")
    }

//    override suspend fun getWalkingList(): Result<List<Walk>> {
//        TODO("Not yet implemented")
//    }

}