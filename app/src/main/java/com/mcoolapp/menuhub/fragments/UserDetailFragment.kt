package com.mcoolapp.menuhub.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.UserDetailFragmentBinding
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.view.UserEditActivity
import com.mcoolapp.menuhub.viewmodel.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_detail_fragment.*
import java.lang.Exception

class UserDetailFragment : Fragment(), LifecycleOwner {

    companion object {
        public const val USER_ID_KEY = "kjnffskr3knsengskflkse"
        fun newInstance(id: String): UserDetailFragment {
            val fragment = UserDetailFragment()

            val bundle = Bundle()
            bundle.putString(USER_ID_KEY, id)

            fragment.arguments = bundle

            return fragment
        }
    }

    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var binding: UserDetailFragmentBinding
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel.setBaseContext(activity?.baseContext!!)
        binding = UserDetailFragmentBinding.inflate(layoutInflater).apply {
            viewmodel = userViewModel
            lifecycleOwner = this@UserDetailFragment
        }

        userID = arguments?.getString(USER_ID_KEY, "def") ?: "nul"
        System.out.println("userID = " + userID)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //userViewModel.loadUser(userID)
        editProfileButtonUserDetail.setOnClickListener {
            val intent = Intent(activity, UserEditActivity::class.java)
            intent.putExtra(USER_ID_KEY, userID)
            startActivity(intent)
        }

        showMenuButton.setOnClickListener {
            val bundle = Bundle()
            println("userViewModel.getUserMenuID().value = "+ userViewModel.getUserMenuID().value)
            bundle.putString(MenuFragment.MENU_ID, userViewModel.getUserMenuID().value)
            findNavController().navigate(R.id.action_userDetailFragment4_to_menuFragment, bundle)
        }

        showChatsButton.setOnClickListener {
            findNavController().navigate(R.id.action_userDetailFragment4_to_chatListFragment)
        }

        startChatWithUserButton.setOnClickListener {

        }

    }

    val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }


    private fun registerToServer() {
        val userRepository = UserRepository()
        userRepository.setBaseContext(requireContext())
        var userID = ""
        if (FirebaseAuth.getInstance().currentUser != null)
            userID = FirebaseAuth.getInstance().currentUser!!.uid

        userRepository.getUser(userID)
            .subscribeOn(Schedulers.io())
            .subscribe({


            }, {
                println("error in UserDetailFragment getUser -> " + it.message)
            })
            .addTo(disposable)
    }

    override fun onResume() {
        super.onResume()
        userViewModel.loadUser(userID)
    }



}