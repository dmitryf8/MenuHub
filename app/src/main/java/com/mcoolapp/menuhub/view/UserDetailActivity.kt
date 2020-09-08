package com.mcoolapp.menuhub.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ActivityUserDetailBinding
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.services.MHubFCMService
import com.mcoolapp.menuhub.utils.isConnectedToNetwork
import com.mcoolapp.menuhub.viewmodel.UserViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : AppCompatActivity(), LifecycleOwner {

    val disposable = CompositeDisposable()
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var viewModel: UserViewModel
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)

        if (isConnectedToNetwork()) {
            System.out.println("Internet connection alive")
        } else {
            System.out.println("Internet connection dead")
        }

        val intent = getIntent()
        id = intent.getStringExtra("ID")!!
        System.out.println("------------------------------------- id from intent in userdetailactivity = " + id + "    -----------------------------------------")

        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        viewModel.setBaseContext(this)
        System.out.println("111111111111111111111111111111111111")
        binding.setLifecycleOwner(this)
        System.out.println("2222222222222222222222222222222222222")
        binding.viewmodel = viewModel
        System.out.println("333333333333333333333333333333333333")

        startChatWithUserButton.setOnClickListener {
            viewModel.loadUser((id)) }

        //editProfileButtonUserDetail.setOnClickListener { startUserEditActivity() }
        initFCM()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUser(id)
    }

    fun startUserEditActivity(v: View) {
        val intent: Intent = Intent(this, UserEditActivity::class.java)
        startActivity(intent)
    }

    fun startEditMenuActivity(v: View) {
        val intent: Intent = Intent(this, CreateMenuItemActivity::class.java)
        startActivity(intent)
    }

    fun initFCM() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("mylog", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token


                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("mylog", msg)

                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
            })
    }



    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

   /** private fun showErrorDialog(s: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(s)
            // if the dialog is cancelable
            .setCancelable(true)
            // positive button text and action
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> finish()
            })
            // negative button text and action
            .setNegativeButton("Close", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Attention!")
        // show alert dialog
        alert.show()
    } **/

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    fun sendMessage() {
        val message = Message()
        val userRepository = UserRepository()
        userRepository.setBaseContext(this)
        userRepository.getUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribeOn(Schedulers.io())
            .subscribe({
                message.messageType = ChatConstants.MESSAGE_CONTENT_TEXT
                message.messageContent = "this is test? just test"

                val b = Bundle()
                val b1 = Bundle()
                b1.putString("messageType", message.messageType)
                b1.putString("messageContent", message.messageContent)
                b.putBundle("data", b1)

                val remoteMessage = RemoteMessage(b)

                FirebaseMessaging.getInstance().send(remoteMessage)


            }, {
                println("error in MainActivity getUser -> " + it.message)
            })
            .addTo(disposable)

    }


}
