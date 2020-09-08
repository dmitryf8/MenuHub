package com.mcoolapp.menuhub.services
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository.ChatRepository
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.view.UserDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class MHubFCMService : FirebaseMessagingService(){
    override fun onCreate() {
        super.onCreate()
        println("MHubFCMService onCreate")


    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.senderId}")
        println("To: " + remoteMessage.to )
        println("Data: " + remoteMessage.data)
        println(remoteMessage.messageId)






        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

           // sendNotification(it.body!!, remoteMessage.senderId!!)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token" +
                ": $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]


        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }


    private fun sendRegistrationToServer(token: String?) {

        val sharedPreference =  getSharedPreferences(MainActivity.SHARED_PREFERENCE,Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("fcmToken", token)
        editor.apply()

        Log.d(TAG, "sendRegistrationTokenToServer($token) in mHubService")
    }

    val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, senderID: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(NOTIFICATION_MARKER, senderID)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)



        val channelId = "defaultChannelId"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_menu_share)
            .setContentTitle("menuHub")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
        public const val NOTIFICATION_MARKER = "xkfslrgsjljslrjgo4e8877euew3i939ehdojosiejif"
    }

    fun registerToChat() {
        val chatRepository = ChatRepository()
        chatRepository.setContext(baseContext)
        chatRepository.subscribeToChat()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("received messages")
                val messageList = it
                val userIDList = arrayListOf<String>()
                val userNameList = arrayListOf<String>()
                var senderID = ""
                for (m in messageList) {
                    if (!userIDList.contains(m.senderID) && (m.senderID != FirebaseAuth.getInstance().currentUser!!.uid)) userIDList.add(m.senderID)
                    if (!userNameList.contains(m.senderName)&& (m.senderID != FirebaseAuth.getInstance().currentUser!!.uid)) userNameList.add(m.senderName)
                    if (userIDList.size == 1) senderID = userIDList[0]
                    val userNames = userNameList.joinToString()
                    if (userIDList.size > 0)
                        sendNotification("Новые сообщения: " + userIDList.size + " от " + userNames, senderID)
                }
            }, {
                println("error in MHubFCMService -> " + it.stackTrace.contentToString())
            })
            .addTo(disposable)
    }
}