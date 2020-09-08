package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentChatListBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.ChatListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuViewPagerAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.ChatViewModel
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_menu.*


class ChatListFragment : Fragment() {
    private var userID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val chatViewModel by viewModels<ChatViewModel>()
    private lateinit var binding: FragmentChatListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatViewModel.setBaseContext(activity?.baseContext!!)

        arguments?.let {
        }


        binding = FragmentChatListBinding.inflate(layoutInflater).apply {
            viewmodel = chatViewModel
            lifecycleOwner = this@ChatListFragment
        }


        chatViewModel.getChatDataList().observeForever {
            binding.chatListRecyclerView.setHasFixedSize(true)
            binding.chatListRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            binding.chatListRecyclerView.isNestedScrollingEnabled = false
            binding.chatListRecyclerView.adapter = ChatListAdapter(it!!, findNavController())
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("super.onViewCreated(view, savedInstanceState)")
        (activity as MainActivity).setTitleText(getString(R.string.messageList))
        chatViewModel.setUserID(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MenuFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}