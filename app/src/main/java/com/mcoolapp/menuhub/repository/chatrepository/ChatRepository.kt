package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.Chat
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.services.MHubFCMService
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatRepository() {

    private val firebaseChatRepository = FirebaseChatRepository()
    private lateinit var context: Context

    fun sendMessage(message: Message, chatID: String): Observable<Boolean> {
        return Observable.create { emitter ->
            firebaseChatRepository.saveMessage(message)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val messageID = it
                    message.id = messageID

                    firebaseChatRepository.addMessageIDToChat(messageID, chatID)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            println("message id added to chatList")
                        }, {
                            println("message id not added to chatList")
                        })
                        .addTo(disposable)
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                })

        }
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }


    fun getMessageListWithID(list: List<String>): Observable<List<Message>> {
        return Observable.create { emitter ->
            firebaseChatRepository.getMessageList(list)
                .subscribeOn(Schedulers.io())
                .subscribe({

                    println("firebaseChatRepository.getMessageList(list) it.size ->" + it.size)
                    emitter.onNext(it)
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                })
        }
    }

    /** fun subscribeToChats(userID: String){
    println(" subscribeToChats(" + userID + ")")
    firebaseChatRepository.remoteDB.collection(FirebaseChatRepository.MESSAGE_COLLECTION)
    .whereArrayContains("userIDList", userID)
    .addSnapshotListener { value, error ->
    if (value != null) {

    println("messages was updated " + value.documentChanges.get(0).document.data)
    val userIDList = arrayListOf<String>()
    for (i in value.documentChanges) {
    if (!userIDList.contains(i.document["id"] as String)) {
    userIDList.add(i.document["id"] as String)

    }
    }
    if (userIDList.size == 1) {
    val userName = value.documentChanges.last().document.get("userName")
    sendNotification(
    context,
    "Получено " + value.documentChanges.size + " новых сообщений от "
    + userName,
    userIDList[0]
    )
    } else {
    sendNotification(
    context,
    "OOOO" + value.documentChanges.size.toString() + " новых сообщений "
    + value.documentChanges,
    ""
    )
    }
    }

    if (error != null) println("error -> " + error.message)

    }

    }
     **/
    fun getChatList(chatIDList: List<String>): Observable<List<Chat>> {
        return Observable.create { emitter ->
            println("chatIDList in ChatRepository ->" + chatIDList)
            firebaseChatRepository.getChatList(chatIDList)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    println("chatList in ChatRepository ->" + it)
                    emitter.onNext(it)
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                })
                .addTo(disposable)
        }
    }


    private fun sendNotification(context: Context, messageBody: String, senderID: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MHubFCMService.NOTIFICATION_MARKER, senderID)
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

    fun setContext(context: Context) {
        this.context = context
        firebaseChatRepository.setContext(context)
    }

    fun subscribeToChat(): Observable<List<Message>> {
        return Observable.create { emitter ->
            firebaseChatRepository.subscribeToChats()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    println("messageList in chatRepository = " +it.size)
                    emitter.onNext(it)
                }, {

                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
        }
    }

}