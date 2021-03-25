package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.databinding.ActivityUserListBinding
import com.mcoolapp.menuhub.model.chat.Chat
import com.mcoolapp.menuhub.model.chat.ChatData
import com.mcoolapp.menuhub.model.chat.MessageData
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository.ChatRepository
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.utils.observeDouble
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.channels.consumesAll
import java.sql.Array

class ChatViewModel : ViewModel(), LifecycleOwner {
    private var userID = MutableLiveData<String>()
    private var userName = MutableLiveData<String>()
    private var chatIDList = MutableLiveData<List<String>>()
    private var chList = MutableLiveData<List<Chat>>()
    private var progressBarVisibility = MutableLiveData<Int>()
    private var chatDataListLD = MutableLiveData<List<ChatData>>()
    private var messageDataListLD = MutableLiveData<List<MessageData>>()

    private val chatRepository = ChatRepository()
    val userRepository = UserRepository()
    var chat = Chat()

    private lateinit var context: Context

    init {
        progressBarVisibility.value = View.GONE
    }

    fun getChatDataList(): MutableLiveData<List<ChatData>> {
        return chatDataListLD
    }

    fun getUserName(): MutableLiveData<String> {
        return userName
    }

    fun getMessageDataList(): MutableLiveData<List<MessageData>> {
        return messageDataListLD
    }

    fun getUserID(): MutableLiveData<String> {
        return userID
    }

    fun getChatIDList(): MutableLiveData<List<String>> {
        return chatIDList
    }

    fun getChatList(): MutableLiveData<List<Chat>> {
        return chList
    }

    fun setUserID(id: String) {
        userID.value = id

        userRepository.setBaseContext(context)
        userRepository.getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val confidentialInfoID = it!!.confidentialID
                userRepository.getConfidentialInfo(confidentialInfoID)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        val chatList = it!!.chatIDList
                        println(chatList)
                        chatIDList.value = chatList
                        bind(chatList)
                    }, {

                    })
                    .addTo(disposable)

            }, {
                println("error in setUserId in ChatViewModel -> " + it.stackTrace.contentToString())
            })
            .addTo(disposable)
    }

    private fun bind(chatIDList: List<String>) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        chatRepository.getChatList(chatIDList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chList.value = it
                val chatList = it
                println("chatList =  " + chatList)

                var chatDataList = arrayListOf<ChatData>()
                val userIDList: ArrayList<String> = arrayListOf<String>()
                var userList: List<User> = listOf<User>()
                val messageIDList = arrayListOf<String>()
                var messageList = listOf<Message>()

                for (chat in it) {
                    val uidList = chat.userIDList
                    for (userID in uidList) {
                        if (userID != currentUserID && !userIDList.contains(userID)) userIDList.add(
                            userID
                        )
                    }
                    messageIDList.add(chat.lastMessageID)
                }

                println(" userIDList in chatviewmodel" + userIDList)

                println(" messageIDList in chatviewmodel" + messageIDList)

                userRepository.getUserList(userIDList)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        println("getting userList in ChatViewModel = " + it!!.size)
                        userList = it
                        for (u in userList) println("u = " + u.id)
                        for (i in userList) println("userName = " + i.userName)
                        chatRepository.getMessageListWithID(messageIDList)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                println(it)
                                messageList = it!!
                                chatDataList = arrayListOf<ChatData>()
                                for (chat in chatList) {
                                    val chatData = ChatData()
                                    chatData.chatID = chat.id
                                    val user = getUserFromList(chat.userIDList, userList)
                                    chatData.chatName = user.userName

                                    val imageWithBucket =
                                        ImageWithBucket(user.userPhotoId, "bucket" + user.id)
                                    chatData.imageWithBucket = imageWithBucket
                                    println("chatData.imageWithBucket.imageID = " + imageWithBucket.imageId)
                                    println("chatData.imageWithBucket.bucketName = " + imageWithBucket.bucketName)
                                    val lastMessage =
                                        getMessageContentFromList(chat.lastMessageID, messageList)
                                    chatData.lastMessageText = lastMessage.messageContent
                                    chatData.time = lastMessage.timestamp.toDate().toString()
                                    chatDataList.add(chatData)
                                }
                                chatDataListLD.value = chatDataList

                            }, {
                                println("Error in chatViewModel bind getMessageList -> " + it.stackTrace.contentToString())
                            })
                            .addTo(disposable)
                    }, {
                        println("Error in chatViewModel bind getUserList -> " + it.stackTrace.contentToString())
                    })
                    .addTo(disposable)

            }, {
                println("getChatList error in chatViewmodel --> " + it)
            }).addTo(disposable)
    }

    fun setBaseContext(context: Context) {
        System.out.println("setBaseContext in userviewmodel")
        this.context = context
        chatRepository.setContext(context)
        userRepository.setBaseContext(context)
    }

    fun sendMessage(message: Message): Observable<Boolean> {
        return Observable.create { emitter ->
            println("sendMessage in ChatViewModel -> " + message.messageContent)
            println("chatID -> " + chatID)
            chatRepository.sendMessage(message, chatID)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    println("message sended")
                    emitter.onNext(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
                .addTo(disposable)

        }
    }

    var chatID = ""

    fun setMessageDataList(chatID: String) {

        this.chatID = chatID
        chatRepository.getChatList(listOf(chatID))
            .subscribeOn(Schedulers.io())
            .subscribe({

                chat = it[0]
                var anotherUserID = ""
                for (id in chat.userIDList) {
                    if (id != FirebaseAuth.getInstance().currentUser!!.uid) {
                        anotherUserID = id
                    }
                }
                userRepository.getUser(anotherUserID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        userName.value = it!!.userName
                    }, {
                        println("error in ChatViewModel userRepository.getUser(anotherUserID) -> " + it.message)
                    })
                    .addTo(disposable)
                println("getted chat List 1 pcs" + chat.lastMessageID)
                val messageIDList = chat.messageIDList
                chatRepository.getMessageListWithID(messageIDList)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        val messageList = it
                        println("size of list messsages" + messageList.size)

                        val messageDataList = arrayListOf<MessageData>()
                        if (it.size > 0) {
                            val userIDList = arrayListOf<String>()
                            for (m in it) {
                                if (!userIDList.contains(m.senderID))
                                    userIDList.add(m.senderID)
                            }
                            userRepository.getUserList(userIDList)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    val userList = it

                                    for (m in messageList) {
                                        val messageData = MessageData()
                                        messageData.description = m.description
                                        val user = getUserFromList(listOf(m.senderID), userList!!)
                                        messageData.imageWithBucket =
                                            ImageWithBucket(user.userPhotoId, "bucket" + user.id)
                                        messageData.messageContent = m.messageContent
                                        messageData.messageID = m.id
                                        messageData.messageStatus = m.messageStatus
                                        messageData.messageType = m.messageType
                                        messageData.senderID = m.senderID
                                        val hours = m.timestamp.toDate().hours.toString()
                                        var minutes = m.timestamp.toDate().minutes.toString()
                                        if (minutes.length == 1) minutes = "0" + minutes
                                        messageData.time =
                                            hours + ":" +
                                                    minutes
                                        messageDataList.add(messageData)
                                    }
                                    println(messageDataList.size.toString() + " messagedata list size ---<<<")
                                    messageDataListLD.value = messageDataList
                                }, {
                                    println("error in userRepository.getUserList(userIDList)  in setMessageDataList")
                                })
                                .addTo(disposable)
                        }

                    }, {
                        messageDataListLD.value = listOf<MessageData>()
                        println("error in chatRepository.getMessageListWithID(messageIDList) -> " + it.message)
                    })
                    .addTo(disposable)

            }, {
                println("error in fun setMessageDataList(chatID: String) chatID = " + chatID)
            })
            .addTo(disposable)
    }

    fun addMessageData(messageData: MessageData) {

        if (messageDataListLD.value != null) {
            var tmpList = messageDataListLD.value
            var mdList = listOf<MessageData>()
            if (tmpList!!.size < 10) {
                if (tmpList.size == 1) mdList = listOf(messageData, tmpList[0]) else
                mdList = tmpList.toMutableList().asReversed().plusElement(messageData).asReversed()
            } else {
                mdList = tmpList.subList(0, tmpList.size - 2)
                mdList = mdList.toMutableList().asReversed().plusElement(messageData).asReversed()
            }
            messageDataListLD.value = mdList
        }
    }
        private fun getUserFromList(idList: List<String>, userList: List<User>): User {
            var user = User()
            val curUsID = FirebaseAuth.getInstance().currentUser!!.uid
            for (u in userList) {
                println("u in userList -> " + u.id)
                if ((u.id in idList) && (u.id != curUsID)) {
                    user = u

                }
            }
            return user
        }

        private fun getMessageContentFromList(
            messageID: String,
            messageList: List<Message>
        ): Message {
            var message = Message()
            for (m in messageList) {
                if (m.id == messageID) message = m
            }
            return message
        }


        override fun getLifecycle(): Lifecycle {
            TODO("Not yet implemented")
        }

        private val disposable = CompositeDisposable()

        fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
            compositeDisposable.add(this)
        }

        override fun onCleared() {
            super.onCleared()
            disposable.dispose()
        }

    }