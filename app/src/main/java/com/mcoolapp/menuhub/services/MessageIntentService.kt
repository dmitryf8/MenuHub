package com.mcoolapp.menuhub.services

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository.ChatRepository
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private const val ACTION_CHAT_LISTEN = "dfnlmvlkmlisdnfrnslj842938hsj48828fn"

private const val USER_ID = "sk94j4k3mgt0b0mkp8l;9[0ll0ik9,k33292d74bvnmz"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class MessageIntentService : IntentService("MessageIntentService") {

    val chatRepository = ChatRepository()
    var userID = ""

    override fun onHandleIntent(intent: Intent?) {
        println("MessageIntentService onHandleIntent")
        when (intent?.action) {

            ACTION_CHAT_LISTEN -> {

                userID = intent.getStringExtra(USER_ID)
                println("MessageIntentService ACTION_CHAT_LISTEN userID = " + userID)
                handleActionChatListen(userID)


            }

        }
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun handleActionChatListen(userID: String) {
        this.userID = userID
        println("MessageIntentService handleActionChatListen")

        chatRepository.setContext(baseContext)
        println("MessageIntentService handleActionChatListen 2")
        chatRepository.subscribeToChat()
            .subscribeOn(Schedulers.io())
            .subscribe({
                println("MessageIntentService chatRepository.subscribeToChat() ")
                var userIDList = arrayListOf<String>()
                for (m in it) {
                    if (m.senderID != userID) {
                        userIDList.add(m.senderID)
                    }

                }
                if (userIDList.size > 0)
                    sendNotification(baseContext, "Новых сообщений: " + userIDList.size.toString(), "")
            }, {
                println("MessageIntentService error in private fun handleActionChatListen(userID: String) -> " + it.stackTrace.contentToString())
            })
            .addTo(disposable)
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
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
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

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        @JvmStatic
        fun startActionChatListen(context: Context, userID: String) {
            println("MessageIntentService fun startActionChatListen")
            val intent = Intent(context, MessageIntentService::class.java).apply {
                action = ACTION_CHAT_LISTEN
                putExtra(USER_ID, userID)
            }
            context.startService(intent)
        }

    }
}
