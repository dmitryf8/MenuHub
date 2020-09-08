package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.Chat
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.repository.DataBase
import com.mcoolapp.menuhub.services.MHubFCMService
import io.reactivex.Observable


class FirebaseChatRepository() {
    private lateinit var context: Context
    private var dataBase = DataBase.INSTANCE

    companion object {
        const val CHAT_COLLECTION = "chat"
        const val MESSAGE_COLLECTION = "message"
    }


    public val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    fun saveMessage(message: Message): Observable<String> {
        return Observable.create { emitter ->
            remoteDB.collection(MESSAGE_COLLECTION)
                .add(message.toHashMap())
                .addOnSuccessListener {
                    val documentID = it.id
                    remoteDB.collection(MESSAGE_COLLECTION)
                        .document(documentID)
                        .update("id", documentID)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onNext(documentID)

                                if (!emitter.isDisposed)
                                    emitter.onComplete()
                            }
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                            if (!emitter.isDisposed)
                                emitter.onComplete()
                        }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }


        }
    }

    fun updateMessage(message: Message): Observable<Boolean> {
        return Observable.create { emitter ->
            remoteDB.collection(MESSAGE_COLLECTION)
                .document(message.id)
                .update(message.toHashMap())
                .addOnSuccessListener {
                    emitter.onNext(true)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it.fillInStackTrace())
                    if (!emitter.isDisposed) emitter.onComplete()
                }

        }
    }

    fun getMessageList(idList: List<String>): Observable<List<Message>> {
        return Observable.create { emitter ->
            var messageList = arrayListOf<Message>()
            println("message id list -> " + idList)
            if (idList.size > 0) {
                remoteDB.collection(MESSAGE_COLLECTION)
                    .whereIn("id", idList.takeLast(10))
                    .addSnapshotListener { v, e ->


                        messageList = arrayListOf()
                        val data = v!!
                            .documents
                        println("data.size -> " + data.size)

                        /**val changedDataSize = v.documentChanges.size
                        sendNotification("Новых сообщений: " + changedDataSize.toString(), ChatConstants.CHAT_COLLECTION )
                         **/


                        for (item in data) {
                            val m = Message(item.data!!)
                            messageList.add(m)
                            println("messageContent" + m.messageContent)
                        }
                        val mList = messageList.sortedBy {
                            it.timestamp
                        }.asReversed()
                        emitter.onNext(mList)
                        if (e != null) emitter.onError(e.fillInStackTrace())
                    }
            } else {
                emitter.onNext(listOf())
            }


        }
    }

    fun getChatList(idList: List<String>): Observable<List<Chat>> {
        return Observable.create { emitter ->
            var chatList = arrayListOf<Chat>()
            remoteDB.collection(CHAT_COLLECTION)
                .whereIn("id", idList)
                .addSnapshotListener { value, error ->
                    println("QuerySnapshor -> " + value!!.documents.toList())
                    chatList = arrayListOf<Chat>()
                    val data = value.documents
                    println(data)
                    for (item in data) {
                        chatList.add(Chat(item.data!!))
                    }
                    emitter.onNext(chatList)
                    if (error != null) {
                        emitter.onError(error.fillInStackTrace())
                    }

                }

        }
    }

    fun createChat(chat: Chat): Observable<String> {
        return Observable.create { emitter ->
            remoteDB.collection(CHAT_COLLECTION)
                .add(chat.toHashMap())
                .addOnSuccessListener {
                    val docID = it.id
                    remoteDB.collection(CHAT_COLLECTION)
                        .document(docID)
                        .update("id", docID)
                        .addOnSuccessListener {
                            emitter.onNext(docID)
                            if (!emitter.isDisposed)
                                emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                            if (!emitter.isDisposed)
                                emitter.onComplete()
                        }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun addMessageIDToChat(messageID: String, chatID: String): Observable<Boolean> {
        return Observable.create { emitter ->
            println("addmessageTo chat messageid ->" + messageID + " chat id-> " + chatID)
            remoteDB.collection(CHAT_COLLECTION)
                .document(chatID)
                .get()
                .addOnSuccessListener {
                    val data = it.data
                    val chat = Chat(data!!)
                    chat.messageIDList = chat.messageIDList.plusElement(messageID)
                    chat.lastMessageID = messageID
                    remoteDB.collection(CHAT_COLLECTION)
                        .document(chat.id)
                        .update(chat.toHashMap())
                        .addOnSuccessListener {
                            println("chat updated")
                            emitter.onNext(true)
                            if (!emitter.isDisposed)
                                emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                            if (!emitter.isDisposed)
                                emitter.onComplete()
                        }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

        }
    }


    fun setContext(context: Context) {
        this.context = context
    }

    fun subscribeToChats(): Observable<List<Message>> {
        return Observable.create {emitter ->
            remoteDB.collection(MESSAGE_COLLECTION)
                .whereArrayContains("userIDList", FirebaseAuth.getInstance().currentUser!!.uid)
                .addSnapshotListener { value, error ->
                    println("subscribeToChat in fbur")
                    val changesList = value!!.documentChanges
                    val messageList = arrayListOf<Message>()
                    for (ch in changesList){
                        messageList.add(Message(ch.document.data))
                    }
                    println("messageList.size = " + messageList.size)
                    emitter.onNext(messageList)

                    if (error != null) emitter.onError(error.fillInStackTrace())
                }

        }
    }
    private fun sendNotification(messageBody: String, marker: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MHubFCMService.NOTIFICATION_MARKER, marker)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )


        val channelId = "defaultChannelId"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_menu_share)
            .setContentTitle("menuHub")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}