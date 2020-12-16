package com.rn1.gogoyo.model.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*
import kotlin.coroutines.suspendCoroutine

interface GogoyoDataSource {

    suspend fun getImageUri(filePath: String): Result<String>

    suspend fun getVideoUri(uri: Uri): Result<String>

    suspend fun getAudioUri(uri: Uri): Result<String>

    suspend fun login(id: String, name: String): Result<Boolean>

    fun getLiveUserById(id: String): MutableLiveData<Users>

    fun getLiveUserFriendStatusById(id: String): MutableLiveData<List<Friends>>

    suspend fun getAllUsers(id: String?): Result<List<Users>>

    suspend fun updateUser(user: Users): Result<Users>

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

    suspend fun setUserFriend(userId: String, friend: Friends): Result<Friends>

    suspend fun getChatRoom(userId: String, friendId: String): Result<Chatroom>

    suspend fun updateChatRoom(chatroom: Chatroom): Result<Boolean>

    fun getUserChatList(userId: String): MutableLiveData<List<Chatroom>>

    suspend fun getChatRoomListWithUserInfo(list: List<Chatroom>): Result<List<Chatroom>>

    suspend fun getChatRoomMessages(chatroomId: String): Result<List<Messages>>

    fun getLiveChatRoomMessages(chatroomId: String): MutableLiveData<List<Messages>>

    suspend fun sendMessage(chatroomId: String, message: Messages): Result<Boolean>


}