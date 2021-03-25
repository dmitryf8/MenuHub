package com.mcoolapp.menuhub.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.view.UserDetailActivity
import kotlinx.android.synthetic.main.activity_email_password.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        (activity as MainActivity).setRightButtonInvisible()
        sign_up_button.setOnClickListener {
            createAccount(emailEditText.text.toString(), passwordEditText.text.toString())
        }

        sign_in_button.setOnClickListener {
            signInWithEmailPassword(emailEditText.text.toString(), passwordEditText.text.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment()
    }

    fun createAccount(email: String, password: String) {
        activity?.let {
            firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("mytag", "createUserWithEmail:success")
                        val user = firebaseAuth!!.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("mytag", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            it, " Неверный логин/пароль ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun signInWithEmailPassword(email: String, password: String) {


        activity?.let {
            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
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
                            it, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    fun updateUI(user: FirebaseUser?) {
        System.out.println("user login succesfull id = " +user!!.uid)
    }
}