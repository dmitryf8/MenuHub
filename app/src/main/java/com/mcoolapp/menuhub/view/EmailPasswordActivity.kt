package com.mcoolapp.menuhub.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.services.MessageIntentService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_email_password.*


class EmailPasswordActivity : Activity() {

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_email_password)
        firebaseAuth = FirebaseAuth.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        println("firebaseAuth!!.currentUser!!.uid" + firebaseAuth!!.currentUser?.uid)
        if (user != null) updateUI(user)
        sign_up_button.setOnClickListener {
            createAccount(emailEditText.text.toString(), passwordEditText.text.toString())
        }

        sign_in_button.setOnClickListener {
            signInWithEmailPassword(emailEditText.text.toString(), passwordEditText.text.toString())
        }

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
    fun createAccount(email: String, password: String) {
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("mytag", "createUserWithEmail:success")

                    val user = User(firebaseAuth!!.currentUser!!.uid)
                    val userRepository = UserRepository()
                    userRepository.setBaseContext(this)
                    userRepository.createUser(user)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            signInWithEmailPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                        }, {
                            println("error in EmailPasswordActivity in createUser -> " +it.message)
                        }
                        )
                        .addTo(disposable)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("mytag", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, " Registration error! ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    fun signInWithEmailPassword(email: String, password: String) {


            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d("mylog", "signInWithEmail:success")
                        val user = firebaseAuth!!.currentUser
                        if (user != null)
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("mylog", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

    }

    fun updateUI(user: FirebaseUser?) {
        var uID = ""
        if (user != null) {
            username.setText(user.displayName)
            userID.setText(user.uid)
            uID = user.uid
        }
        if (uID != "") {
            MessageIntentService.startActionChatListen(this, uID)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("ID", user!!.uid)
        startActivity(intent)
        this.finish()

    }
}
